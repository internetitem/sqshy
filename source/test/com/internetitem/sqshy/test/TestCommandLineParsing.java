package com.internetitem.sqshy.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.internetitem.sqshy.RunSqshy;
import com.internetitem.sqshy.config.args.CommandLineParseException;
import com.internetitem.sqshy.config.args.CommandLineParser;
import com.internetitem.sqshy.config.args.ParsedCommandLine;
import com.internetitem.sqshy.config.args.StringValue;

public class TestCommandLineParsing {

	private CommandLineParser parser1;

	public TestCommandLineParsing() {
		parser1 = RunSqshy.buildCommandLineParser();
		parser1.addArg(new StringValue("test", "Test Value", new String[] { "-t", "--test" }, true));
	}

	@Test
	public void testBooleans() throws CommandLineParseException {
		assertTrue(parser1.parse(new String[] { "-h" }).getBoolValue("help"));
		assertTrue(parser1.parse(new String[] { "--help" }).getBoolValue("help"));
		assertFalse(parser1.parse(new String[] { "--help=false" }).getBoolValue("help"));
		assertFalse(parser1.parse(new String[] { "--help", "false" }).getBoolValue("help"));
		assertTrue(parser1.parse(new String[] { "--help", "--username", "bob" }).getBoolValue("help"));
	}

	@Test(expected = CommandLineParseException.class)
	public void testInvalidParameter() throws CommandLineParseException {
		parser1.parse(new String[] { "--help", "--foo" });
	}

	@Test(expected = CommandLineParseException.class)
	public void testMissingValue() throws CommandLineParseException {
		parser1.parse(new String[] { "--help", "--username" });
	}

	@Test
	public void testRequiredValue() throws CommandLineParseException {
		assertEquals("foo", parser1.parse(new String[] { "--help", "--username", "foo" }).getValue("username"));
	}

	@Test
	public void testConsume() throws CommandLineParseException {
		ParsedCommandLine consumed = parser1.parse(new String[] { "--help", "false", "--", "blah", "--arg" });
		assertFalse(consumed.getBoolValue("help"));
		assertArrayEquals(consumed.getExtraArgs().toArray(new String[0]), new String[] { "blah", "--arg" });
	}

	@Test
	public void testOptional() throws CommandLineParseException {
		assertTrue(parser1.parse(new String[] { "--test" }).hasValue("test"));
		assertEquals(null, parser1.parse(new String[] { "--test" }).getValue("test"));
		assertEquals("foo", parser1.parse(new String[] { "--test", "foo" }).getValue("test"));
		assertEquals("foo", parser1.parse(new String[] { "--test=foo" }).getValue("test"));
		assertEquals("", parser1.parse(new String[] { "--test=" }).getValue("test"));
		assertTrue(parser1.parse(new String[] { "--test", "--username", "foo" }).hasValue("test"));
		assertEquals("", parser1.parse(new String[] { "--test=", "--username", "foo" }).getValue("test"));
	}

	@Test
	public void testList() throws CommandLineParseException {
		assertArrayEquals(parser1.parse(new String[] { "--properties", "one=1", "two=2" }).getList("properties").toArray(new String[0]), new String[] { "one=1", "two=2" });
		assertArrayEquals(parser1.parse(new String[] { "--properties", "one=1", "two=2", "//", "--help" }).getList("properties").toArray(new String[0]), new String[] { "one=1", "two=2" });
		assertArrayEquals(parser1.parse(new String[] { "--properties=;", "one=1", "two=2", ";", "--help" }).getList("properties").toArray(new String[0]), new String[] { "one=1", "two=2" });
	}
}
