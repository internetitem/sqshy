package com.internetitem.sqshy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;

import jline.console.ConsoleReader;

public class SqshyRepl {

	private ConsoleReader reader;
	private Map<String, String> variables;
	private Connection conn = null;

	public SqshyRepl(ConsoleReader reader, Map<String, String> variables) {
		this.reader = reader;
		this.variables = variables;
	}

	public void connect(String driverClass, String url, String username, String password, Map<String, String> connectionProperties) throws Exception {
		if (driverClass == null) {
			throw new Exception("No JDBC Driver Class specified");
		}
		System.err.println("Connecting to URL " + url + " with driver " + driverClass);
		try {
			Class.forName(driverClass);
		} catch (Exception e) {
			throw new Exception("Unable to load JDBC Driver Class [" + driverClass + "]: " + e.getMessage(), e);
		}

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
	}

	public void start() throws Exception {
		String line;
		while ((line = reader.readLine("sql> ")) != null) {
			reader.println("You said [" + line + "]");
		}
	}

}
