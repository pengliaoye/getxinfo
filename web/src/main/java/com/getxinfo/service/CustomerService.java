package com.getxinfo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	
	@Value("${database.jdbc_password}")
	private String password;
	
	public void printPwd(){
		System.out.println(password);
	}

}
