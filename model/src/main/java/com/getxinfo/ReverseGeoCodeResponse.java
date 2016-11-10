package com.getxinfo;

import lombok.Value;

@Value
public class ReverseGeoCodeResponse {
	
	private int status;	
	private ReverseGeoCodeResult  result;
	
	@Value
	public static class ReverseGeoCodeResult {
		
		private String formatted_address;		
		private AddressComponent addressComponent;
		
		@Value
		public static class AddressComponent {
			
			private String adcode;
			
		}
	}	

}
