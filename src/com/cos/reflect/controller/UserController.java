package com.cos.reflect.controller;

import com.cos.reflect.anno.RequstMapping;
import com.cos.reflect.controller.dto.JoinDto;
import com.cos.reflect.controller.dto.LoginDto;

public class UserController {
	
	@RequstMapping("/join")
	public String join(JoinDto dto) {
		System.out.println("join() : " + dto);
		return "/";
	}
	
	@RequstMapping("/login")
	public String login(LoginDto dto) {
		System.out.println("login() : " + dto);
		return "/";
	}
	
	@RequstMapping("/user")
	public String user() {
		System.out.println("user()");
		return "/";
	}
	
	@RequstMapping("/hello")
	public String hello() {
		System.out.println("hello()");
		return "/";
	}

}
