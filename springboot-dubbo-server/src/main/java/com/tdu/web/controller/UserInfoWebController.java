package com.tdu.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tdu.service.UserInfoService;

@Controller
@RequestMapping("/api/userinfo")
public class UserInfoWebController {

	@Autowired
	UserInfoService userInfoService;

	@RequestMapping("/hello")
	@ResponseBody
	public String hello() {
		return userInfoService.hello();
	}
}
