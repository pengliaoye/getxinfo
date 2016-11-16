package com.getxinfo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cn.gb2260.Division;
import cn.gb2260.GB2260;

public class IdCard {

	private static final String ID_CARD_REGEX = "^\\d{6}(18|19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|X)$";

	private String id;
	private String area;
	private Date birthDay;
	private String sex;

	public IdCard(String id) {
		this.id = id;
	}

	public void parse() throws ParseException {
		validate();
		String areaCode = id.substring(0, 6);
		GB2260 gb = new GB2260();
		Division division = gb.getDivision(areaCode);
		area = division.getName();

		String date = id.substring(6, 14);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		birthDay = format.parse(date);

		String sexStr = id.substring(14, 17);
		int sexInt = Integer.valueOf(sexStr);
		if ((sexInt % 2) == 0) {
			sex = "女";
		} else {
			sex = "男";
		}
	}

	public void validate() {
		Pattern pattern = Pattern.compile(ID_CARD_REGEX);
		if (!pattern.matcher(id).matches()) {
			throw new IllegalArgumentException("格式不合法");
		}

		int[] map = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		int sum = 0;
		for (int i = 0; i < map.length; i++) {
			int idCharVal = Character.getNumericValue(id.charAt(i));
			sum += (map[i] * idCharVal);
		}
		String vchars = "10X98765432";
		int vidx = sum % 11;
		char vchar = vchars.charAt(vidx);
		if (vchar != id.charAt(id.length() - 1)) {
			throw new IllegalArgumentException("校验码错误");
		}
	}

	public String getArea() {
		return area;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public String getSex() {
		return sex;
	}

}
