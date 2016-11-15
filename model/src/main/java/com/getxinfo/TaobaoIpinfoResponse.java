package com.getxinfo;

import lombok.Value;

@Value
public class TaobaoIpinfoResponse {

	private int code;
	private Data data;

	@Value
	public static class Data {

		private String city_id;
		private String city;

	}

}
