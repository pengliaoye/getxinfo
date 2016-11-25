package com.getxinfo.util;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class HmacUtil {
	
	public static String getHmaSHA256key() {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("HmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		SecretKey secretKey = keyGenerator.generateKey();
		byte[] encodedKey = secretKey.getEncoded();
		String str = Base64.getEncoder().encodeToString(encodedKey);
		return str;
	}

}
