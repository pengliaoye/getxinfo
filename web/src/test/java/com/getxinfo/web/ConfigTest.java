package com.getxinfo.web;

import org.junit.Assert;
import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class ConfigTest {
		
	@Test
	public void testConfig(){
		Config conf = ConfigFactory.load();
		int bar1 = conf.getInt("foo.bar");
		Assert.assertEquals(10, bar1);
	}
	
	@Test
	public void testInt(){
		int i = 1049999990;
		float f = (float)i;
		String str = String.format("%f", f);
		System.out.println(str);
	}

}
