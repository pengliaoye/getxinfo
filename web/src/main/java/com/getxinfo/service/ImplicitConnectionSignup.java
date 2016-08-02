package com.getxinfo.service;

import javax.sql.DataSource;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

public class ImplicitConnectionSignup implements ConnectionSignUp{
	
	private DataSource dataSource;

	public ImplicitConnectionSignup(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public String execute(Connection<?> connection) {
		String providerUserId = connection.getKey().getProviderUserId();
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
		manager.setDataSource(dataSource);

		UserDetails user = new User(providerUserId, "", AuthorityUtils.createAuthorityList("ROLE_USER"));
		manager.createUser(user);
		return providerUserId;
	}

}
