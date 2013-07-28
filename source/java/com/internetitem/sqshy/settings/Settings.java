package com.internetitem.sqshy.settings;

import java.util.HashMap;
import java.util.Map;

import com.internetitem.sqshy.ConnectionManager;
import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.util.StringUtil;

public class Settings {

	private Output originalLogger;
	private Output logger;
	private ConnectionManager connectionManager;
	private Map<String, String> variables;

	public Settings() {
		this.variables = new HashMap<>();
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void addVariables(Map<String, String> newVariables) {
		if (newVariables == null) {
			return;
		}
		variables.putAll(newVariables);
	}

	public Output getOutput() {
		return logger;
	}

	public void init(Output logger, ConnectionManager connectionManager) {
		this.logger = logger;
		this.originalLogger = logger;
		this.connectionManager = connectionManager;
	}

	public String getStringValue(String name, String defaultValue) {
		if (variables.containsKey(name)) {
			return variables.get(name);
		} else {
			return defaultValue;
		}
	}

	public boolean hasValue(String name) {
		return variables.containsKey(name);
	}

	public String getStringValue(String name) {
		return getStringValue(name, null);
	}

	public boolean getBooleanValue(String name, boolean defaultValue) {
		String stringValue = getStringValue(name);
		if (stringValue == null) {
			return defaultValue;
		} else {
			return StringUtil.parseBoolean(stringValue);
		}
	}

	public boolean getBooleanValue(String name) {
		return getBooleanValue(name, true);
	}

	public int getIntValue(String name, int defaultValue) {
		String stringValue = getStringValue(name);
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

	public Map<String, String> getVariables() {
		return variables;
	}

	public String getPrompt() {
		return getStringValue("prompt", "sql> ");
	}

	public String getPrompt2() {
		return getStringValue("prompt2", "> ");
	}

	public String getGocmd() {
		return getStringValue("gocmd", "\\go");
	}

	public String getDelimiter() {
		return getStringValue("delimiter", ";");
	}

	public void setValue(String name, String value) {
		variables.put(name, value);
	}

}
