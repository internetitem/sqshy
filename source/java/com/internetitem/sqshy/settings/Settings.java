package com.internetitem.sqshy.settings;

import java.util.LinkedList;

import com.internetitem.sqshy.util.StringUtil;

public class Settings {
	private LinkedList<SettingsSet> settingsSets;

	public Settings() {
		this.settingsSets = new LinkedList<>();
	}

	public void addSet(SettingsSet set) {
		settingsSets.addFirst(set);
	}

	public String getStringValue(String name, String defaultValue) {
		for (SettingsSet set : settingsSets) {
			String value = set.getValue(name);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;
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

}
