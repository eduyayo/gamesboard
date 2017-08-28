package com.pigdroid.hub.model.message;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameMessageTest {

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}
	
	@Test
	public void testTwoConsecutive() throws Exception {
		GameMessage one = null;
		GameMessage two = null;
		one = new GameMessage();
		two = new GameMessage();
		Assert.assertFalse(one.getId().equals(two.getId()));
		Assert.assertTrue(Long.parseLong(one.getId()) < Long.parseLong(two.getId()));
	}

//	@Test
//	public void testCreate10000() throws Exception {
//		String[] list = new String[10000];
//		for (int i = 0; i < 10000;  i += 5) {
//			list[i] = new GameMessage().getId();
//			list[i + 1] = new GameMessage().getId();
//			list[i + 2] = new GameMessage().getId();
//			list[i + 3] = new GameMessage().getId();
//			list[i + 4] = new GameMessage().getId();
//		}
//		Set<String> set = new HashSet<String>();
//		for (int i = 0; i < 10000; i++) {
//			set.add(list[i]);
//		}
//		Assert.assertEquals(10000, set.size());
//	}
	
//	@Test
//	public void test10000() throws Exception {
//		ArrayList<String> list = new ArrayList<String>(10000);
//		for (int i = 0; i < 10000;  i++) {
//			list.add(Long.toString(System.nanoTime()));
//			try {
//				Thread.sleep(0L, 1);
//			} catch (Exception e) {
//				
//			}
//		}
//		Set<String> set = new HashSet<String>();
//		set.addAll(list);
//		Assert.assertEquals(10000, set.size());
//	}
	
}
