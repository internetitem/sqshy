package com.internetitem.sqshy.config.args;

public abstract class ValuedArgument<T> extends CommandLineArgument<T> {

	private boolean optional;
	private T defaultValue;

	public ValuedArgument(String name, String description, String[] names, boolean optional, T defaultValue) {
		super(name, description, names);
		this.optional = optional;
		this.defaultValue = defaultValue;
	}

	@Override
	protected T parse(CommandLineState args) throws CommandLineParseException {
		String argValue = args.next();
		if (argValue == null) {
			if (optional) {
				return defaultValue;
			} else {
				throw new CommandLineParseException(getName(), "missing required value");
			}
		} else {
			return parseValue(argValue);
		}
	}

	protected abstract T parseValue(String value) throws CommandLineParseException;
}
