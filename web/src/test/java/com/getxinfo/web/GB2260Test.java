package com.getxinfo.web;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import org.junit.Test;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import cn.gb2260.Division;
import cn.gb2260.GB2260;

public class GB2260Test {

	@Test
	public void testGB2260() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader = mapper.readerFor(Map.class);
		FileInputStream fis = new FileInputStream("gbcode_geonames.json");
		Map<String, Object> geoNameMap = reader.readValue(fis);

		GB2260 gb = new GB2260();
		List<Division> provinces = gb.getProvinces();
		for (Division province : provinces) {
			String code = province.getCode();
			String name = province.getName();

			printNameInfo(geoNameMap, 1, code, name);

			List<Division> cities = gb.getPrefectures(code);
			for (Division city : cities) {
				String cityCode = city.getCode();
				String cityName = city.getName();

				printNameInfo(geoNameMap, 2, cityCode, cityName);

				List<Division> districts = gb.getCounties(cityCode);
				for (Division district : districts) {
					String districtCode = district.getCode();
					String districtName = district.getName();
					System.out.println("  |  |--" + districtCode + " " + districtName);
				}
			}
		}

	}

	private void printNameInfo(Map<String, Object> geoNameMap, int level, String code, String name) {
		Integer geonameId = null;
		String geoname = null;
		Map<String, Object> nameMap = (Map) geoNameMap.get(code);
		if (nameMap != null) {
			geonameId = (Integer) nameMap.get("geonameId");
			geoname = (String) nameMap.get("geoname");
		}

		if (level == 2) {
			System.out.print("  |--");
		}

		if (geonameId != null) {
			System.out.println(code + "(" + geonameId + ", " + geoname + ") " + name);
		} else {
			System.out.println(code + " " + name);
		}
	}

	@Test
	public void testGeoName() throws Exception {
		WebService.setUserName("pengliaoye");
		String lang = "zh";
		Style style = Style.SHORT;
		List<Integer> list = new ArrayList<>();
		list.add(1819730);
		list.add(1668284);
		list.add(1821275);
		for (Integer val : list) {
			String geoName = WebService.get(val, lang, style.name()).getName();
			System.out.println(val + "  " + geoName);
		}
	}

	private String getGeoName(List<Map<Integer, Object>> statesList, Integer val) throws Exception {
		String geoName = null;
		List<Map<Integer, Object>> allCities = new ArrayList<>();
		for (Map<Integer, Object> map : statesList) {
			allCities.add(map);
			List<Map<Integer, Object>> children = (List) map.get("children");
			if (children != null) {
				allCities.addAll(children);
			}
		}
		for (Map<Integer, Object> map : allCities) {
			Integer geoNameId = (Integer) map.get("geoNameId");
			if (val == geoNameId) {
				geoName = (String) map.get("name");
				break;
			}
		}
		if (geoName == null) {
			WebService.setUserName("pengliaoye");
			String lang = "zh";
			Style style = Style.SHORT;
			geoName = WebService.get(val, lang, style.name()).getName();
		}
		return geoName;
	}

	private List<Map<Integer, Object>> getGeonameChildren(List<Map<Integer, Object>> statesList, String name) {
		for (Map<Integer, Object> stateMap : statesList) {
			String geoname = (String) stateMap.get("name");
			if (name.contains(geoname.substring(0, 2))) {
				return (List<Map<Integer, Object>>) stateMap.get("children");
			}
		}
		return null;
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

			WebService.setUserName("pengliaoye");
			String lang = "zh";
			Style style = Style.SHORT;

			List<Map<String, Object>> statesList = new ArrayList<>();
			int[] municipality = new int[] { 2038349, 1796231, 1792943, 1814905 };
			ToponymSearchResult stateResult = WebService.children(1814991, lang, style);
			List<Toponym> states = stateResult.getToponyms();
			for (Toponym state : states) {
				System.out.println(state.getName());

				Map<String, Object> stateMap = new HashMap<>();
				stateMap.put("geoNameId", state.getGeoNameId());
				stateMap.put("name", state.getName());

				List<Map<String, Object>> citiesList = new ArrayList<>();
				boolean contain = IntStream.of(municipality).anyMatch(x -> x == state.getGeoNameId());
				if (!contain) {
					ToponymSearchResult cityResult = WebService.children(state.getGeoNameId(), lang, style);
					for (Toponym city : cityResult.getToponyms()) {
						Map<String, Object> cityMap = new HashMap<>();
						cityMap.put("geoNameId", city.getGeoNameId());
						cityMap.put("name", city.getName());
						citiesList.add(cityMap);
					}
				}

				stateMap.put("children", citiesList);
				statesList.add(stateMap);
			}

			int[] geoids = new int[] { 1819730, 1668284, 1821275 };
			for (int i = 0; i < geoids.length; i++) {
				Map<String, Object> stateMap = new HashMap<>();
				stateMap.put("geoNameId", geoids[i]);
				stateMap.put("name", WebService.get(geoids[i], lang, style.name()).getName());
				statesList.add(stateMap);
			}

			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			FileOutputStream out = new FileOutputStream("geonames.json");
			writer.writeValue(out, statesList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
