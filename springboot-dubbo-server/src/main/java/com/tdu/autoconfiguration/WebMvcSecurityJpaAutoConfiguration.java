package com.tdu.autoconfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties.Headers;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Configuration
@AutoConfigureBefore(SpringBootWebSecurityConfiguration.class)
@EnableConfigurationProperties
@ConditionalOnClass({ EnableWebSecurity.class, AuthenticationEntryPoint.class })
@ConditionalOnMissingBean(WebSecurityConfiguration.class)
@ConditionalOnWebApplication
@EnableWebSecurity
public class WebMvcSecurityJpaAutoConfiguration {
	@Bean
	@ConditionalOnMissingBean
	public AuthenticationManager webMvcSecurityAuthenticationManager(AuthenticationConfiguration auth)
			throws Exception {
		return auth.getAuthenticationManager();
	}
	
	private static List<String> DEFAULT_IGNORED = Arrays.asList("/css/**", "/js/**", "/images/**", "/**/favicon.ico",
			"/download/**", "/webjars/**", "/resources/**");
	
	@Bean
	@ConditionalOnMissingBean({ IgnoredPathsWebSecurityConfigurerAdapter.class })
	public IgnoredPathsWebSecurityConfigurerAdapter ignoredPathsWebSecurityConfigurerAdapter() {
		return new IgnoredPathsWebSecurityConfigurerAdapter();
	}

	public static void configureHeaders(HeadersConfigurer<?> configurer, SecurityProperties.Headers headers)
			throws Exception {
		if (headers.getHsts() != Headers.HSTS.NONE) {
			boolean includeSubdomains = headers.getHsts() == Headers.HSTS.ALL;
			HstsHeaderWriter writer = new HstsHeaderWriter(includeSubdomains);
			writer.setRequestMatcher(AnyRequestMatcher.INSTANCE);
			configurer.addHeaderWriter(writer);
		}
		if (!headers.isContentType()) {
			configurer.contentTypeOptions().disable();
		}
		if (!headers.isXss()) {
			configurer.xssProtection().disable();
		}
		if (!headers.isCache()) {
			configurer.cacheControl().disable();
		}
		if (!headers.isFrame()) {
			configurer.frameOptions().disable();
		}
	}

	public static List<String> getIgnored(SecurityProperties security) {
		List<String> ignored = new ArrayList<String>(security.getIgnored());
		ignored.addAll(DEFAULT_IGNORED);//合并默认路径
		if (ignored.contains("none")) {
			ignored.remove("none");
		}
		return ignored;
	}

	@Order(SecurityProperties.IGNORED_ORDER)
	private static class IgnoredPathsWebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {

		@Autowired(required = false)
		private ErrorController errorController;

		@Autowired
		private SecurityProperties security;

		@Autowired
		private ServerProperties server;

		@Override
		public void configure(WebSecurity builder) throws Exception {
		}

		@Override
		public void init(WebSecurity builder) throws Exception {
			List<String> ignored = getIgnored(this.security);
			if (this.errorController != null) {
				ignored.add(normalizePath(this.errorController.getErrorPath()));
			}
			String[] paths = this.server.getPathsArray(ignored);
			if (!ObjectUtils.isEmpty(paths)) {
				builder.ignoring().antMatchers(paths);
			}
		}

		private String normalizePath(String errorPath) {
			String result = StringUtils.cleanPath(errorPath);
			if (!result.startsWith("/")) {
				result = "/" + result;
			}
			return result;
		}
	}
	
	@Bean
	@ConditionalOnMissingBean
	public WebMvcSecurityConfiguration WebMvcSecurityConfiguration(AuthenticationConfiguration auth)
			throws Exception {
		return new WebMvcSecurityConfiguration();
	}

	protected static class WebMvcSecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Autowired
		private Environment environment;

		@Autowired
		protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
		}
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			Boolean eanbleCsrf = environment.getProperty("security.enable-csrf", Boolean.class, false);
			if (eanbleCsrf) {
				String csrfExcludes = environment.getProperty("security.csrf.excludes", String.class, null);
				if (StringUtils.hasText(csrfExcludes)) {
					CsrfProtectionMatcher csrfProtectionMatcher = new CsrfProtectionMatcher(csrfExcludes);
					http.csrf().requireCsrfProtectionMatcher(csrfProtectionMatcher);
				}
			} else {
				http.csrf().disable();
			}

			http.authorizeRequests().antMatchers(getPermitAllUrlAntMatchers()).permitAll().anyRequest().authenticated();

			http.formLogin()
				.loginPage("/login.html")
				.loginProcessingUrl("/login")
				.failureUrl("/login.html")
				.permitAll()
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/");
		}

		private class CsrfProtectionMatcher implements RequestMatcher {
			private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
			private List<AntPathRequestMatcher> excludeMatchers = new ArrayList<AntPathRequestMatcher>();

			public CsrfProtectionMatcher(String csrfExcludes) {
				if (StringUtils.hasText(csrfExcludes)) {
					String[] excludes = StringUtils
							.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(csrfExcludes));
					if (excludes != null && excludes.length > 0) {
						for (String exclude : excludes) {
							this.excludeMatchers.add(new AntPathRequestMatcher(exclude));
						}
					}
				}
			}

			public boolean matches(HttpServletRequest request) {
				boolean allowed = allowedMethods.matcher(request.getMethod()).matches();
				if (allowed) {
					return false;
				} else {
					for (AntPathRequestMatcher matcher : excludeMatchers) {
						if (matcher.matches(request)) {
							return false;
						}
					}
					return true;
				}
			}
		}

		private String[] getPermitAllUrlAntMatchers() {
			List<String> result = new ArrayList<String>();
			result.add("/webjars/**");
			result.add("/resources/**");
			return result.toArray(new String[] {});
		}
	}
}
