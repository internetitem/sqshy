package com.internetitem.sqshy.config.args;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParsedCommandLine {

	private List<String> extraArgs;
	private Map<String, Object> values;

	ParsedCommandLine(List<String> extraArgs, Map<String, Object> values) {
		this.extraArgs = extraArgs;
		this.values = values;
	}

	public List<String> getExtraArgs() {
		return extraArgs;
	}

	public boolean hasValue(String name) {
		return values.containsKey(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String name) {
		return (T) values.get(name);
	}

	public int getIntValue(String name, int defaultValue) {
		if (hasValue(name)) {
			return ((Integer) values.get(name)).intValue();
		} else {
			return defaultValue;
		}
	}

	public boolean getBoolValue(String name) {
		if (hasValue(name)) {
			return ((Boolean) values.get(name)).booleanValue();
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(String name) {
		if (hasValue(name)) {

			return ((List<String>) values.get(name));
		} else {
			return Collections.emptyList();
		}
	}
}
