package com.getxinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "database")
public class ConnectionSettings {

	private String jdbc_url;

	private String jdbc_user;
	
	private String uuid;

	public String getJdbc_url() {
		return jdbc_url;
	}

	public void setJdbc_url(String jdbc_url) {
		this.jdbc_url = jdbc_url;
	}

	public String getJdbc_user() {
		return jdbc_user;
	}

	public void setJdbc_user(String jdbc_user) {
		this.jdbc_user = jdbc_user;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
}