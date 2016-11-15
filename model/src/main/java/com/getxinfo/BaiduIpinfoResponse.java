package com.getxinfo;

import lombok.Value;

@Value
public class BaiduIpinfoResponse {
	
	private int status;
	private Content content;
	
	@Value
	public static class Content {
		
		private AddressDetail address_detail;
		
		@Value
		public static class AddressDetail {
			
			private int city_code;
			private String city;
			
		}
		
	}

}
