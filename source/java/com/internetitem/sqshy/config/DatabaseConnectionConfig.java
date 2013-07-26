package com.internetitem.sqshy.config;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConnectionConfig {

	private String alias;
	private String driverClass;
	private String url;
	private String username;
	private String password;
	private Map<String, String> properties;
	private Map<String, String> variables;

	public DatabaseConnectionConfig() {
		this.properties = new HashMap<>();
		this.variables = new HashMap<>();
	}

	public DatabaseConnectionConfig(String driverClass, String url, String username, String password) {
		this();
		this.driverClass = driverClass;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}
}
