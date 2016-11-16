package com.getxinfo.util;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class IdCardTest {
		
	@Test
	public void testIdCard() throws ParseException{
		IdCard idCard = new IdCard("44522419910904455X");
		idCard.parse();
		String area = idCard.getArea();
		Date birthDay = idCard.getBirthDay();
		String sex = idCard.getSex();		
		System.out.println(area);
		System.out.println(birthDay);
		System.out.println(sex);
	}

}
