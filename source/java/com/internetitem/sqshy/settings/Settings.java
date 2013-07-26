package com.internetitem.sqshy.settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.config.DatabaseConnectionConfig;
import com.internetitem.sqshy.config.DriverMatch;
import com.internetitem.sqshy.util.DatabaseUtil;
import com.internetitem.sqshy.util.StringUtil;

public class Settings {

	private Output originalLogger;
	private Output logger;
	private Connection conn;
	private Map<String, String> variables;
	private List<DriverMatch> driverInfos;
	private Set<String> loadedClasses;
	List<DatabaseConnectionConfig> savedConnections;

	public Settings() {
		this.variables = new HashMap<>();
		this.loadedClasses = new HashSet<>();
	}

	public void addVariables(Map<String, String> newVariables) {
		if (newVariables == null) {
			return;
		}
		variables.putAll(newVariables);
	}

	public void connect(String alias, String driverClass, String url, String username, String password, Map<String, String> connectionProperties) throws CommandException {
		DatabaseUtil.closeConnection(conn);
		conn = null;

		OUTER: if (alias != null) {
			if (savedConnections == null || savedConnections.isEmpty()) {
				throw new CommandException("alias specified but no saved connections");
			}
			for (DatabaseConnectionConfig dcc : savedConnections) {
				if (alias.equals(dcc.getAlias())) {
					if (driverClass == null) {
						driverClass = dcc.getDriverClass();
					}
					if (url == null) {
						url = dcc.getUrl();
					}
					if (username == null) {
						username = dcc.getUsername();
					}
					if (password == null) {
						password = dcc.getPassword();
					}
					if (connectionProperties == null) {
						connectionProperties = dcc.getProperties();
					}
					Map<String, String> variables = dcc.getVariables();
					if (variables != null) {
						addVariables(variables);
					}
					break OUTER;
				}
			}
			throw new CommandException("connect specified but alias not found");
		}

		if (driverClass == null) {
			findDriver(url);
		}
		if (driverClass != null) {
			logger.connectMessage("Connecting to URL " + url + " with driver " + driverClass);
			loadDriver(driverClass, false);
		} else {
			logger.connectMessage("Connecting to URL " + url);
		}

		try {
			if (connectionProperties != null && !connectionProperties.isEmpty()) {
				Properties props = new Properties();
				props.putAll(connectionProperties);
				if (!props.containsKey("user") && username != null) {
					props.put("user", username);
				}
				if (!props.containsKey("password") && password != null) {
					props.put("password", password);
				}
				conn = DriverManager.getConnection(url, props);
			} else if (username != null || password != null) {
				conn = DriverManager.getConnection(url, username, password);
			} else {
				conn = DriverManager.getConnection(url);
			}
		} catch (SQLException e) {
			throw new CommandException("Unable to connect: " + e.getMessage());
		}
	}

	public Output getOutput() {
		return logger;
	}

	public Connection getConnection() {
		return conn;
	}

	private void loadDriver(String className, boolean log) {
		if (loadedClasses.contains(className)) {
			return;
		}
		loadedClasses.add(className);
		try {
			Class.forName(className);
			if (log) {
				logger.connectMessage("Loaded driver " + className);
			}
		} catch (Exception e) {
			logger.connectMessage("Unable to load driver " + className + ": " + e.getMessage());
		}
	}

	private void findDriver(String url) {
		logger.connectMessage("Scanning for JDBC Drivers");
		for (DriverMatch match : driverInfos) {
			if (Pattern.compile(match.getMatch()).matcher(url).find()) {
				loadDriver(match.getDriver(), true);
			}
		}
	}

	public void init(List<DriverMatch> driverInfo, List<DatabaseConnectionConfig> savedConnections, Output logger) {
		this.driverInfos = driverInfo;
		this.logger = logger;
		this.originalLogger = logger;
		this.savedConnections = savedConnections;
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
