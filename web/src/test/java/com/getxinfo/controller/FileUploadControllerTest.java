package com.getxinfo.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.getxinfo.config.StorageProperties;

public class FileUploadControllerTest {

	FileUploadController controller;

	@Before
	public void init() {
		StorageProperties properties = new StorageProperties();
		controller = new FileUploadController(properties);
	}

	@Test
	public void testBaseName() {
		String baseName = controller.baseName("C:/Users/pgy/Desktop/log/log4j2.xml", null);
		Assert.assertEquals("log4j2.xml", baseName);
		baseName = controller.baseName("C:/Users/pgy/Desktop/log/log4j2.xml", ".xml");
		Assert.assertEquals("log4j2", baseName);
	}

	@Test
	public void testTrimFileName() {
		String fileName = controller.trimFileName(".123.456.. ");
		Assert.assertEquals("123.456", fileName);
	}

	@Test
	public void testGetUniqueFileName() {
		String name = "123.txt";
		for (int i = 0; i < 5; i++) {
			name = controller.getUniqueFileName(null, name, 0, null);
			System.out.println(name);
		}
	}

}
