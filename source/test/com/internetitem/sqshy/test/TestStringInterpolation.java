package com.internetitem.sqshy.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.internetitem.sqshy.util.StringUtil;

public class TestStringInterpolation {

	private Map<String, String> variables;

	public TestStringInterpolation() {
		this.variables = new HashMap<>();
		variables.put("one", "1");
		variables.put("two", "2");
		variables.put("nullish", null);
		variables.put("compound.variable", "long value");
	}

	@Test
	public void testSimpleInterpolation() {
		assertEquals("1", StringUtil.interpolate("${one}", variables));
		assertEquals("one 1", StringUtil.interpolate("one ${one}", variables));
		assertEquals("one 1.", StringUtil.interpolate("one ${one}.", variables));
	}

	@Test
	public void testNullValue() {
		assertEquals("one .", StringUtil.interpolate("one ${nullish}.", variables));
	}

	@Test
	public void testMultipleValues() {
		assertEquals("one 1 and another long value.", StringUtil.interpolate("one ${one} and another ${compound.variable}.", variables));
	}
}
