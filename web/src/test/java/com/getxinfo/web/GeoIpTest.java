package com.getxinfo.web;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.getxinfo.BaiduIpinfoResponse;
import com.getxinfo.QQIpinfoResponse;
import com.getxinfo.ReverseGeoCodeResponse;
import com.getxinfo.TaobaoIpinfoResponse;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;

public class GeoIpTest {

	@Test
	public void testGeopIp() throws IOException, GeoIp2Exception  {
		// A File object pointing to your GeoIP2 or GeoLite2 database
		File database = ResourceUtils.getFile("classpath:GeoLite2-City.mmdb");

		// This creates the DatabaseReader object, which should be reused across
		// lookups.
		DatabaseReader reader = new DatabaseReader.Builder(database).build();

		// InetAddress ipAddress = InetAddress.getByName("58.17.133.8");
		// InetAddress ipAddress = InetAddress.getByName("115.28.184.47");
		 InetAddress ipAddress = InetAddress.getByName("139.129.216.131");

		// Replace "city" with the appropriate method for your database, e.g.,
		// "country".
		CityResponse response = reader.city(ipAddress);

		Country country = response.getCountry();
		System.out.println(country.getGeoNameId());
		System.out.println(country.getIsoCode()); // 'US'
		System.out.println(country.getName()); // 'United States'
		System.out.println(country.getNames().get("zh-CN")); // '美国'

		Subdivision subdivision = response.getMostSpecificSubdivision();
		System.out.println(subdivision.getName()); // 'Minnesota'
		System.out.println(subdivision.getIsoCode()); // 'MN'

		City city = response.getCity();
		System.out.println(city.getGeoNameId());
		System.out.println(city.getNames().get("zh-CN")); // 'Minneapolis'

		Postal postal = response.getPostal();
		System.out.println(postal.getCode()); // '55455'

		Location location = response.getLocation();
		System.out.println(location.getLatitude()); // 44.9733
		System.out.println(location.getLongitude()); // -93.2323
					
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://api.map.baidu.com/geocoder/v2/")
														   .queryParam("ak", "keY3TMAqKN3N0sdXn1Wb8g4ApHDhcLtt")
														   .queryParam("location", latLng)
														   .queryParam("output","json")
														   .queryParam("coordtype", "wgs84ll");		
		RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, new MediaType("text", "*")));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		
		ReverseGeoCodeResponse reverseGeoCodeResponse = restTemplate.getForObject(builder.toUriString(), ReverseGeoCodeResponse.class);
		System.out.println(reverseGeoCodeResponse.getStatus());
		System.out.println(reverseGeoCodeResponse.getResult().getFormatted_address());
		System.out.println(reverseGeoCodeResponse.getResult().getAddressComponent().getAdcode());
		
		// qq
		QQIpinfoResponse qqIpinfoResponse = restTemplate.getForObject(String.format("http://apis.map.qq.com/ws/location/v1/ip?ip=%s&key=W52BZ-CJIRF-UATJV-NNTXU-WWPSS-ZTFBJ", "139.129.216.131"), QQIpinfoResponse.class);
		System.out.println(qqIpinfoResponse.getResult().getAd_info().getAdcode());
		System.out.println(qqIpinfoResponse.getResult().getAd_info().getCity());
		
		// taobao
		TaobaoIpinfoResponse taobaoIpinfoResponse = restTemplate.getForObject(String.format("http://ip.taobao.com/service/getIpInfo.php?ip=%s", "139.129.216.131"), TaobaoIpinfoResponse.class);
		System.out.println(taobaoIpinfoResponse.getData().getCity_id());
		System.out.println(taobaoIpinfoResponse.getData().getCity());
		// baidu		
		String baiduLocUrl = String.format("https://api.map.baidu.com/location/ip?ip=%s&ak=keY3TMAqKN3N0sdXn1Wb8g4ApHDhcLtt", "139.129.216.131");
		BaiduIpinfoResponse baiduIpinfoResponse = restTemplate.getForObject(baiduLocUrl, BaiduIpinfoResponse.class);
		System.out.println(baiduIpinfoResponse.getContent().getAddress_detail().getCity_code());
		System.out.println(baiduIpinfoResponse.getContent().getAddress_detail().getCity());
		
		GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyB-mE0UlWMolk0_m6U3-tgy7li-KGnDz6Y");
		try {
			GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).await();
			System.out.println(results[0].formattedAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTelSegment() throws ScriptException{
		RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
		String str = restTemplate.getForObject("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=13389600105", String.class);	
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("nashorn");
        Map<String, Object> map = new HashMap<>();
        engine.put("map", map);
        engine.eval(str+ "for(var key in __GetZoneResult_){map.put(key, __GetZoneResult_[key]);}");
        System.out.println(map);;
	}

}
