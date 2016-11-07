package com.getxinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.getxinfo.config.StorageProperties;

@SpringBootApplication
//@EnableEurekaClient
@EnableConfigurationProperties(StorageProperties.class)
public class Application {	

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}