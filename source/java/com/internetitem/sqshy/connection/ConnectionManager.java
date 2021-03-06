package com.internetitem.sqshy.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.config.DatabaseConnectionConfig;
import com.internetitem.sqshy.config.DriverMatch;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.DatabaseUtil;

public class ConnectionManager {

	private String username;
	private String password;
	private String url;
	private String driverClass;
	private String alias;
	private Properties properties;

	private DriverLoader driverLoader;
	private List<DatabaseConnectionConfig> savedConnections;
	private Settings settings;
	private Connection conn;

	public ConnectionManager(Settings settings, Set<DriverMatch> driverInfo, Set<String> driverDirs, List<DatabaseConnectionConfig> savedConnections) {
		this.settings = settings;
		this.savedConnections = savedConnections;
		this.driverLoader = new DriverLoader(settings, driverDirs, driverInfo);
	}

	public Connection getConnection() {
		return conn;
	}

	public void reconnect() throws CommandException {
		settings.getOutput().connectMessage("Reconnecting to " + this.url);
		closeConnection(false);
		doConnect(this.username, this.password, this.url, this.properties);
	}

	public void disconnect() {
		if (conn != null) {
			settings.getOutput().connectMessage("Disconnecting from " + this.url);
			closeConnection(false);
		} else {
			settings.getOutput().connectMessage("Not connected");
		}
	}

	public void connect(String alias, String driverClass, String url, String username, String password, Map<String, String> connectionProperties) throws CommandException {
		closeConnection(true);

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
						settings.getVariableManager().addAll(variables);
					}
					this.alias = alias;
					break OUTER;
				}
			}
			throw new CommandException("connect specified but alias not found");
		}

		this.driverClass = driverClass;
		driverLoader.loadDriver(driverClass, url);
		settings.getOutput().connectMessage("Connecting to URL " + url);

		Properties props = null;
		if (connectionProperties != null && !connectionProperties.isEmpty()) {
			props = new Properties();
			props.putAll(connectionProperties);
		}

		doConnect(username, password, url, props);
	}

	private void doConnect(String username, String password, String url, Properties props) throws CommandException {
		try {
			if (props != null && !props.isEmpty()) {
				if (!props.containsKey("user") && username != null) {
					props.put("user", username);
				}
				if (!props.containsKey("password") && password != null) {
					props.put("password", password);
				}
				this.properties = props;
				this.username = username;
				this.password = password;
				this.url = url;
				conn = DriverManager.getConnection(url, props);
			} else if (username != null || password != null) {
				conn = DriverManager.getConnection(url, username, password);
				this.username = username;
				this.password = password;
				this.url = url;
			} else {
				conn = DriverManager.getConnection(url);
				this.url = url;
			}
		} catch (SQLException e) {
			throw new CommandException("Unable to connect: " + e.getMessage());
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getAlias() {
		return alias;
	}

	public void closeConnection(boolean clearVariables) {
		DatabaseUtil.closeConnection(conn);
		conn = null;
		if (clearVariables) {
			this.username = null;
			this.password = null;
			this.url = null;
			this.driverClass = null;
			this.alias = null;
			this.properties = null;
		}
	}

}
