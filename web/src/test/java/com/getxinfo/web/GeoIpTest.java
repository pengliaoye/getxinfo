package com.getxinfo.web;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

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

		InetAddress ipAddress = InetAddress.getByName("58.17.133.8");
		//InetAddress ipAddress = InetAddress.getByName("115.28.184.47");

		// Replace "city" with the appropriate method for your database, e.g.,
		// "country".
		CityResponse response = reader.city(ipAddress);

		Country country = response.getCountry();
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
		GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyB-mE0UlWMolk0_m6U3-tgy7li-KGnDz6Y");
		try {
			GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).await();
			System.out.println(results[0].formattedAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
