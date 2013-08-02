package com.internetitem.sqshy.variables;

import java.util.Set;

import com.internetitem.sqshy.command.CommandException;

public class SimpleVariable implements Variable {

	private boolean writable;
	private String value;

	public SimpleVariable(String value, boolean writable) {
		this.value = value;
		this.writable = writable;
	}

	@Override
	public String getStringValue(String fullName, String partName, String defaultValue) throws CommandException {
		if (partName == null) {
			if (value == null) {
				return defaultValue;
			} else {
				return value;
			}
		} else {
			throw new CommandException(fullName + " does not have property " + partName);
		}
	}

	@Override
	public void setStringValue(String fullName, String partName, String value, boolean userRequest) throws CommandException {
		if (userRequest && !writable) {
			throw new CommandException(fullName + " is read-only");
		} else {
			if (partName != null) {
				throw new CommandException(fullName + " does not have property " + partName);
			} else {
				this.value = value;
			}
		}
	}

	@Override
	public Set<String> getNestedNames() {
		return null;
	}
}
