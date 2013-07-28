package com.internetitem.sqshy.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.internetitem.sqshy.RunSqshy;
import com.internetitem.sqshy.config.args.CommandLineArgument.ArgumentType;
import com.internetitem.sqshy.config.args.CommandLineParseException;
import com.internetitem.sqshy.config.args.CommandLineParser;
import com.internetitem.sqshy.config.args.ParsedCommandLine;

public class TestCommandLineParsing {

	private CommandLineParser parser1;

	public TestCommandLineParsing() {
		parser1 = RunSqshy.buildCommandLineParser();
		parser1.addArg("test", "test", "t", ArgumentType.OptionalArg, "Test Value");
	}

	@Test
	public void testBooleans() throws CommandLineParseException {
		assertTrue(parser1.parse(new String[] { "-h" }).getBoolValue("help"));
		assertTrue(parser1.parse(new String[] { "--help" }).getBoolValue("help"));
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
		assertEquals("foo", parser1.parse(new String[] { "--help", "--username", "foo" }).getStringValue("username"));
	}

	@Test
	public void testConsume() throws CommandLineParseException {
		ParsedCommandLine consumed = parser1.parse(new String[] { "--help", "--", "blah", "--arg" });
		assertTrue(consumed.getBoolValue("help"));
		assertArrayEquals(new String[] { "blah", "--arg" }, consumed.getExtraArgs().toArray(new String[0]));
	}

	@Test
	public void testOptional() throws CommandLineParseException {
		assertTrue(parser1.parse(new String[] { "--test" }).hasStringValue("test"));
		assertEquals(null, parser1.parse(new String[] { "--test" }).getStringValue("test"));
		assertEquals("foo", parser1.parse(new String[] { "--test", "foo" }).getStringValue("test"));
		assertEquals("foo", parser1.parse(new String[] { "--test=foo" }).getStringValue("test"));
		assertEquals("", parser1.parse(new String[] { "--test=" }).getStringValue("test"));
		assertTrue(parser1.parse(new String[] { "--test", "--username", "foo" }).getBoolValue("test"));
		assertEquals("", parser1.parse(new String[] { "--test=", "--username", "foo" }).getStringValue("test"));
	}

	@Test
	public void testList() throws CommandLineParseException {
		assertArrayEquals(new String[] { "one=1", "two=2" }, parser1.parse(new String[] { "--property", "one=1", "two=2" }).getListValues("property").toArray(new String[0]));
		assertArrayEquals(new String[] { "one=1", "two=2" }, parser1.parse(new String[] { "--property", "one=1", "two=2", "--help" }).getListValues("property").toArray(new String[0]));
		assertArrayEquals(new String[] { "one=1", "two=2" }, parser1.parse(new String[] { "--property", "one=1", "--property", "two=2", "--help" }).getListValues("property").toArray(new String[0]));
	}
}
