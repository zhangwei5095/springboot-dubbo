package com.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages={"com.client"})
@SpringApplicationConfiguration(classes = ApplicationDubboClient.class)
public class ApplicationDubboClient {
	@Autowired
	private HttpService httpService;

	@Test
	public void run() {
		long a=0;
		for (int i = 0; i < 100000; i++) {
			long s = System.nanoTime();
			httpService.hello2();
			long e = System.nanoTime();
			a+=(e-s);
		}
		System.out.println("total:"+a/1000d/1000/1000);
	}
}
