package com.getxinfo.web;

import java.util.List;

import org.junit.Test;

import cn.gb2260.Division;
import cn.gb2260.GB2260;

public class GB2260Test {
	
	@Test
	public void testGB2260(){
		GB2260 gb = new GB2260();
		List<Division> provinces = gb.getProvinces();
		for(Division province : provinces){
			String code = province.getCode();
			String name = province.getName();
			System.out.println(code + " " + name);		
			List<Division> cities = gb.getPrefectures(code);
			for(Division city : cities){				
				String cityCode = city.getCode();
				if(cityCode.equals(code)){
					continue;
				}
				String cityName = city.getName();
				System.out.println("  |--" + cityCode + " " + cityName);								
				List<Division> districts = gb.getCounties(cityCode);
				for(Division district : districts){
					String districtCode = district.getCode();
					if(cityCode.equals(districtCode)){
						continue;
					}
					String districtName = district.getName();
					System.out.println("  |  |--" + districtCode + " " + districtName);
				}
			}
		}		
	} 

}
