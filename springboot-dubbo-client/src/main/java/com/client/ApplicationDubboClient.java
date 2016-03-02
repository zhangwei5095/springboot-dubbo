package com.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tdu.service.UserInfoService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationDubboClient.class)
@ImportResource({"classpath:application_custom.xml"})
public class ApplicationDubboClient {

	@Reference
	private UserInfoService userInfoService;
	
	@Test
	public void run(){
		System.out.println(userInfoService.hello());
	}
}
