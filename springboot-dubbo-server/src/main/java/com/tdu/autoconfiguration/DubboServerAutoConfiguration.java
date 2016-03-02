package com.tdu.autoconfiguration;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:application_provider.xml"})
public class DubboServerAutoConfiguration {

	@PostConstruct
	public void aaa(){
		System.err.println("eror");
	}
}
