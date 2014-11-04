package org.eye.util;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FixedSizeSetTest {

	private static FixedSizeSet<String> set;

	@Before
	public void setUp() throws Exception {
		System.out.println("[SET UP]");
		set = new FixedSizeSet<String>(new HashSet<String>(),3);

	}

	@Test
	public void fixSizeTest(){
		set.add("1");
		set.add("2");
		set.add("3");
		set.add("4");
		set.add("5");
		assertEquals(3,set.size());
	}

	@Test
	public void SetBehaviourTest(){
		set.add("4");
		set.add("4");
		set.add("4");
		assertEquals(1,set.size());
	}

	@Test
	public void RemoveTest(){
		set.add("4");
		set.add("5");
		set.remove("5");
		set.remove("4");
		assertTrue(set.isEmpty());
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("[TEAR DOWN]");
	}
}