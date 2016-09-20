package com.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tdu.service.UserInfoService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationDubboClient.class)
@ImportResource({"classpath:application_custom.xml"})
public class ApplicationDubboClient {

	@Autowired
	private UserInfoService userInfoService;
	
	@Test
	public void run(){
		long a=0;
		for(int i=0;i<100000;i++){
			long s=System.nanoTime();
			userInfoService.hello();
			long e=System.nanoTime();
			a+=(e-s);
		}
		System.out.println("total:"+a/1000d/1000/1000);
	}
}
