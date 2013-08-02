package com.internetitem.sqshy.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.util.StringUtil;

public class VariableManager {

	private Map<String, Variable> variables;

	public VariableManager() {
		this.variables = new HashMap<>();
	}

	public void setValue(String name, String newValue, boolean userRequest) throws CommandException {
		String[] parts = name.split("\\.", 2);
		String varName = parts[0];
		String partName = null;
		if (parts.length == 2) {
			partName = parts[1];
		}
		Variable var = variables.get(varName);
		if (var == null) {
			if (partName != null) {
				throw new CommandException("unable to set compound variable " + name);
			} else {
				variables.put(varName, new SimpleVariable(newValue, true));
			}
		} else {
			var.setStringValue(name, partName, newValue, userRequest);
		}
	}

	public void setVariable(String prefix, Variable var) {
		variables.put(prefix, var);
	}

	public List<String> getVariableNames() {
		return new ArrayList<>(variables.keySet());
	}

	public String getValue(String name, String defaultValue) throws CommandException {
		String[] parts = name.split("\\.", 2);
		String varName = parts[0];
		String partName = null;
		if (parts.length == 2) {
			partName = parts[1];
		}
		Variable var = variables.get(varName);
		if (var == null) {
			if (partName != null) {
				throw new CommandException(name + " does not exist");
			} else {
				return defaultValue;
			}
		} else {
			return var.getStringValue(name, partName, defaultValue);
		}
	}

	public boolean getBooleanValue(String name, boolean defaultValue) throws CommandException {
		String stringValue = getValue(name, null);
		if (stringValue == null) {
			return defaultValue;
		} else {
			return StringUtil.parseBoolean(stringValue);
		}
	}

	public boolean getBooleanValue(String name) throws CommandException {
		return getBooleanValue(name, true);
	}

	public int getIntValue(String name, int defaultValue) throws CommandException {
		String stringValue = getValue(name, null);
		if (stringValue == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(stringValue);
			} catch (Exception e) {
				return defaultValue;
			}
		}
	}

	public void addAll(Map<String, String> newValues) throws CommandException {
		if (newValues == null) {
			return;
		}
		for (Entry<String, String> e : newValues.entrySet()) {
			setValue(e.getKey(), e.getValue(), false);
		}
	}

	public Variable getVariable(String name) {
		return variables.get(name);
	}

}
