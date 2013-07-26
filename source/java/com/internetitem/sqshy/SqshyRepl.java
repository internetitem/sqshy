package com.internetitem.sqshy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		reader.println("Connecting to URL " + url + " with driver " + driverClass);
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
			if (conn == null) {
				reader.println("Not connected");
				continue;
			}

			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				stmt.execute(line);
				do {
					rs = stmt.getResultSet();
					if (rs != null) {
						displayResult(rs);
					} else {
						int updateCount = stmt.getUpdateCount();
						reader.println("Affected " + updateCount + " rows");
					}
				} while (stmt.getMoreResults() || stmt.getUpdateCount() != -1);
			} catch (SQLException e) {
				reader.println("Error: " + e.getMessage());
			} finally {
				closeResultSet(rs);
				closeStatement(stmt);
			}
		}
	}

	private void displayResult(ResultSet rs) throws SQLException, IOException {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		reader.println("Query returned " + count + " rows");
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			// Ignore
		}
	}

	public static void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			// Ignore
		}
	}

	public static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			// Ignore
		}
	}
}
