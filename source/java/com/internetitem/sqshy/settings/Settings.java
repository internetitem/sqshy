package com.internetitem.sqshy.settings;

import java.util.ArrayList;
import java.util.List;

import com.internetitem.sqshy.util.StringUtil;

public class Settings {
	private List<SettingsSet> settingsSets;

	public Settings() {
		this.settingsSets = new ArrayList<>();
	}

	public String getStringValue(String name) {
		for (SettingsSet set : settingsSets) {
			String value = set.getValue(name);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public String getStringValue(String name, String defaultValue) {
		String value = getStringValue(name);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	public boolean getBooleanValue(String name) {
		String stringValue = getStringValue(name);
		if (stringValue == null) {
			return false;
		} else {
			return StringUtil.parseBoolean(stringValue);
		}
	}
}
