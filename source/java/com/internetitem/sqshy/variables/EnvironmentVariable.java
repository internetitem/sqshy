package com.internetitem.sqshy.variables;

import java.util.Set;

import com.internetitem.sqshy.command.CommandException;

public class EnvironmentVariable implements Variable {

	@Override
	public String getStringValue(String fullName, String partName, String defaultValue) throws CommandException {
		if (partName == null) {
			return "<environment>";
		}

		String value = System.getenv(partName);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public void setStringValue(String fullName, String partName, String newValue, boolean userRequest) throws CommandException {
		throw new CommandException("environment variables are readonly");
	}

	// Too many to list
	@Override
	public Set<String> getNestedNames() {
		return null;
	}

}
