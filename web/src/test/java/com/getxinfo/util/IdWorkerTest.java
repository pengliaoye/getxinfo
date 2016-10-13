package com.getxinfo.util;

import org.junit.Test;

public class IdWorkerTest {
		
	@Test
	public void testGenId(){
		IdWorker worker = new IdWorker(1, 1);
		for (int i = 0; i < 10; i++) {
			long id = worker.nextId();
			System.out.println(id);
		}
	}

}
