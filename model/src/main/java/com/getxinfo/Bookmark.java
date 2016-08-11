package com.getxinfo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class Bookmark {

	@JsonIgnore
	@ManyToOne
	private Account account;

	@Id
	@GeneratedValue
	@ApiModelProperty(example = "123", value = "Id")
	private Long id;

	public Bookmark() { // jpa only

	}

	public Bookmark(Account account, String uri, String description) {
		this.uri = uri;
		this.description = description;
		this.account = account;
	}
	
	@ApiModelProperty(example = "www.baidu.com", value = "URL")
	public String uri;
	@ApiModelProperty(example = "描述", value = "描述")
	public String description;

	public Account getAccount() {
		return account;
	}

	public Long getId() {
		return id;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getDescription() {
		return description;
	}
}