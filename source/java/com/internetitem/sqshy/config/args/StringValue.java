package com.internetitem.sqshy.config.args;

public class StringValue extends ValuedArgument<String> {

	public StringValue(String name, String description, String[] names, boolean optional, String defaultValue) {
		super(name, description, names, optional, defaultValue);
	}

	public StringValue(String name, String description, String[] names, boolean optional) {
		super(name, description, names, optional, null);
	}

	@Override
	protected String parseValue(String value) {
		return value;
	}

}
