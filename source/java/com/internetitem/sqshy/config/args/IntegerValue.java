package com.internetitem.sqshy.config.args;

public class IntegerValue extends ValuedArgument<Integer> {

	public IntegerValue(String name, String description, String[] names, boolean optional, Integer defaultValue) {
		super(name, description, names, optional, defaultValue);
	}

	@Override
	protected Integer parseValue(String value) throws CommandLineParseException {
		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
			throw new CommandLineParseException(getName(), "invalid integer");
		}
	}

}
