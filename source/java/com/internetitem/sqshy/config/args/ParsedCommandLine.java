package com.internetitem.sqshy.config.args;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.internetitem.sqshy.util.StringUtil;

public class ParsedCommandLine {

	private List<String> extraArgs;
	private Map<String, String> stringValues;
	private Map<String, List<String>> listValues;

	public ParsedCommandLine(List<String> extraArgs, Map<String, String> stringValues, Map<String, List<String>> listValues) {
		this.extraArgs = extraArgs;
		this.stringValues = stringValues;
		this.listValues = listValues;
	}

	public List<String> getExtraArgs() {
		return extraArgs;
	}

	public boolean hasStringValue(String name) {
		return stringValues.containsKey(name);
	}

	public List<String> getListValues(String name) {
		if (listValues.containsKey(name)) {
			return listValues.get(name);
		} else {
			return Collections.emptyList();
		}
	}

	public String getStringValue(String name) {
		return stringValues.get(name);
	}

	public int getIntValue(String name, int defaultValue) {
		String stringValue = getStringValue(name);
		if (stringValue != null) {
			try {
				return Integer.parseInt(stringValue);
			} catch (Exception e) {
				// Ignore
			}
		}
		return defaultValue;
	}

	public boolean getBoolValue(String name) {
		if (hasStringValue(name)) {
			String stringValue = getStringValue(name);
			if (stringValue != null) {
				return StringUtil.parseBoolean(stringValue);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
