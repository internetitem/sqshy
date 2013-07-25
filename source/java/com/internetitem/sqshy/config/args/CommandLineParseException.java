package com.internetitem.sqshy.config.args;

public class CommandLineParseException extends Exception {

	public CommandLineParseException(String argName, String message) {
		super(argName + ": " + message);
	}

}
