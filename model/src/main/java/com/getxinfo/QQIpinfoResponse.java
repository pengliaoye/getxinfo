package com.getxinfo;

import lombok.Value;

@Value
public class QQIpinfoResponse {

	private int status;
	private Result result;

	@Value
	public static class Result {

		private AdInfo ad_info;

		@Value
		public static class AdInfo {

			private String adcode;
			private String city;

		}
	}

}
