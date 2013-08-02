package com.internetitem.sqshy.variables;

import java.util.Set;

import com.internetitem.sqshy.command.CommandException;

public class SystemPropertyVariable implements Variable {

	@Override
	public String getStringValue(String fullName, String partName, String defaultValue) throws CommandException {
		if (partName == null) {
			return "<java>";
		}
		return System.getProperty(partName, defaultValue);
	}

	@Override
	public void setStringValue(String fullName, String partName, String newValue, boolean userRequest) throws CommandException {
		if (partName == null) {
			throw new CommandException(fullName + " is read-only");
		}
		System.setProperty(partName, newValue);
	}

	@Override
	public Set<String> getNestedNames() {
		return System.getProperties().stringPropertyNames();
	}

}
