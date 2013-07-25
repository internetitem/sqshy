package com.internetitem.sqshy.config.args;

public abstract class CommandLineArgument<T> {

	private String name;
	private String[] names;
	private String description;

	public CommandLineArgument(String name, String description, String... names) {
		this.name = name;
		this.description = description;
		this.names = names;
	}

	public String getName() {
		return name;
	}

	public String[] getNames() {
		return names;
	}

	public String getDescription() {
		return description;
	}

	protected abstract T parse(CommandLineState args) throws CommandLineParseException;

}
