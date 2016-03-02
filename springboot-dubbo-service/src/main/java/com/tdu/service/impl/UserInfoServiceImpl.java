package com.tdu.service.impl;

import org.springframework.stereotype.Service;

import com.tdu.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Override
	public String hello() {
		return "hello K l  国人";
	}

}
