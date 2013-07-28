package com.internetitem.sqshy.config.args;

public class CommandLineArgument {

	private String name;
	private String shortName;
	private String longName;
	private ArgumentType argumentType;
	private String description;

	public CommandLineArgument(String name, String shortName, String longName, ArgumentType argumentType, String description) {
		this.name = name;
		this.shortName = shortName;
		this.longName = longName;
		this.argumentType = argumentType;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLongName() {
		return longName;
	}

	public ArgumentType getArgumentType() {
		return argumentType;
	}

	public String getDescription() {
		return description;
	}

	public static enum ArgumentType {
		NoArg,
		OptionalArg,
		RequiredArg,
		List
	}
}
