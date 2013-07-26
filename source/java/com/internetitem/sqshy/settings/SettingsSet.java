package com.internetitem.sqshy.settings;

import java.util.Map;

public class SettingsSet {

	private SettingSource sourceType;
	private String sourceName;
	private Map<String, String> values;

	public SettingsSet(SettingSource sourceType, String sourceName, Map<String, String> values) {
		this.sourceType = sourceType;
		this.sourceName = sourceName;
		this.values = values;
	}

	public SettingSource getSourceType() {
		return sourceType;
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getValue(String name) {
		return values.get(name);
	}

	public enum SettingSource {
		User,
		CommandLine,
		Connection,
		UserConfig,
		DefaultConfig
	}
}
