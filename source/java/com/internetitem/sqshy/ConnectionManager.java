package com.internetitem.sqshy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.config.DatabaseConnectionConfig;
import com.internetitem.sqshy.config.DriverMatch;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.DatabaseUtil;

public class ConnectionManager {

	private List<DriverMatch> driverInfos;
	private Set<String> loadedClasses;
	private List<DatabaseConnectionConfig> savedConnections;
	private Settings settings;
	private Connection conn;

	public ConnectionManager(Settings settings, List<DriverMatch> driverInfo, List<DatabaseConnectionConfig> savedConnections) {
		this.settings = settings;
		this.driverInfos = driverInfo;
		this.savedConnections = savedConnections;
	}

	public Connection getConnection() {
		return conn;
	}

	public void connect(String alias, String driverClass, String url, String username, String password, Map<String, String> connectionProperties) throws CommandException {
		closeConnection();

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
						settings.addVariables(variables);
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
			settings.getOutput().connectMessage("Connecting to URL " + url + " with driver " + driverClass);
			loadDriver(driverClass, false);
		} else {
			settings.getOutput().connectMessage("Connecting to URL " + url);
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

	private void loadDriver(String className, boolean log) {
		if (loadedClasses.contains(className)) {
			return;
		}
		loadedClasses.add(className);
		try {
			Class.forName(className);
			if (log) {
				settings.getOutput().connectMessage("Loaded driver " + className);
			}
		} catch (Exception e) {
			settings.getOutput().connectMessage("Unable to load driver " + className + ": " + e.getMessage());
		}
	}

	private void findDriver(String url) {
		settings.getOutput().connectMessage("Scanning for JDBC Drivers");
		for (DriverMatch match : driverInfos) {
			if (Pattern.compile(match.getMatch()).matcher(url).find()) {
				loadDriver(match.getDriver(), true);
			}
		}
	}

	public void closeConnection() {
		DatabaseUtil.closeConnection(conn);
		conn = null;
	}

}
