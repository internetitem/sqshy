package com.internetitem.sqshy.variables;

import java.util.Set;

import com.internetitem.sqshy.command.CommandException;

public interface Variable {

	String getStringValue(String fullName, String partName, String defaultValue) throws CommandException;

	void setStringValue(String fullName, String partName, String newValue, boolean userRequest) throws CommandException;
	
	Set<String> getNestedNames();
}
