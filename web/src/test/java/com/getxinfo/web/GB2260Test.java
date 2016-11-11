package com.getxinfo.web;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import org.junit.Test;

import cn.gb2260.Division;
import cn.gb2260.GB2260;

public class GB2260Test {

	@Test
	public void testGB2260() throws IOException {

		InputStream input = new FileInputStream("geonames.properties");
		Properties properties = new Properties();
		properties.load(input);

		int i = 0;
		List<String> noGeoNameCityNames = new ArrayList<>();

		GB2260 gb = new GB2260();
		List<Division> provinces = gb.getProvinces();
		for (Division province : provinces) {
			String code = province.getCode();
			String name = province.getName();
			System.out.println(code + " " + name);
			List<Division> cities = gb.getPrefectures(code);
			for (Division city : cities) {
				String cityCode = city.getCode();
				String cityName = city.getName();

				String geonameId = null;
				for(Map.Entry<Object, Object> entry : properties.entrySet()){
					if(entry.getValue().toString().contains(StringUtils.strip(cityName, "市县区"))){
						geonameId = entry.getKey().toString();
						break;
					}
				}
				
				i++;											
				if(geonameId != null){
					System.out.println("  |--" + cityCode + "("+geonameId+") " + cityName);
				} else {
					noGeoNameCityNames.add(cityName);
					System.out.println("  |--" + cityCode + " " + cityName);
				}
				
				List<Division> districts = gb.getCounties(cityCode);
				for (Division district : districts) {
					String districtCode = district.getCode();
					String districtName = district.getName();
					System.out.println("  |  |--" + districtCode + " " + districtName);
				}
			}
		}

		System.out.println(noGeoNameCityNames);
		System.out.println(noGeoNameCityNames.size());
		System.out.println(i);
	}

	@Test
	public void testHierarchy() {
		try {
			WebService.setUserName("pengliaoye");
			List<Toponym> hierarchies = WebService.hierarchy(1808681, "zh", Style.SHORT);
			for (Toponym level : hierarchies) {
				System.out.println(level.getGeoNameId() + "=" + level.getName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGeoNames() {
		try {
			// 1814991 中国
			// 1819730 香港
			// 1668284 台湾
			// 1821275 澳门
			Map<String, String> map = new HashMap<>();

			WebService.setUserName("pengliaoye");
			String lang = "zh";
			Style style = Style.SHORT;

			int[] municipality = new int[] { 2038349, 1796231, 1792943, 1814905 };
			ToponymSearchResult stateResult = WebService.children(1814991, lang, style);
			List<Toponym> states = stateResult.getToponyms();
			for (Toponym state : states) {
				System.out.println(state.getName());
				boolean contain = IntStream.of(municipality).anyMatch(x -> x == state.getGeoNameId());
				if (!contain) {
					ToponymSearchResult cityResult = WebService.children(state.getGeoNameId(), lang, style);
					for (Toponym city : cityResult.getToponyms()) {
						map.put(String.valueOf(city.getGeoNameId()), city.getName());
					}
				}
			}

			int[] geoids = new int[] { 1819730, 1668284, 1821275 };
			for (int i = 0; i < geoids.length; i++) {
				map.put(String.valueOf(geoids[i]), WebService.get(geoids[i], lang, style.name()).getName());
			}

			OutputStream out = new FileOutputStream("geonames.properties");
			Properties properties = new Properties();
			properties.putAll(map);
			properties.store(out, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
