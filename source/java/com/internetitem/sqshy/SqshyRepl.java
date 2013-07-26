package com.internetitem.sqshy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import jline.console.ConsoleReader;

import com.internetitem.sqshy.config.DriverMatch;
import com.internetitem.sqshy.util.DatabaseUtil;

public class SqshyRepl {

	private ConsoleReader reader;
	private Map<String, String> variables;
	private List<DriverMatch> driverInfos;
	private Connection conn = null;

	public SqshyRepl(ConsoleReader reader, Map<String, String> variables, List<DriverMatch> driverInfos) {
		this.reader = reader;
		this.variables = variables;
		this.driverInfos = driverInfos;
	}

	public void connect(String driverClass, String url, String username, String password, Map<String, String> connectionProperties) throws Exception {
		if (driverClass == null) {
			findDriver(url);
		}
		if (driverClass != null) {
			reader.println("Connecting to URL " + url + " with driver " + driverClass);
			try {
				Class.forName(driverClass);
			} catch (Exception e) {
				throw new Exception("Unable to load JDBC Driver Class [" + driverClass + "]: " + e.getMessage(), e);
			}
		} else {
			reader.println("Connecting to URL " + url);
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

	private void findDriver(String url) throws IOException {
		reader.println("Scanning for JDBC Drivers");
		for (DriverMatch match : driverInfos) {
			if (Pattern.compile(match.getMatch()).matcher(url).find()) {
				try {
					Class.forName(match.getDriver());
					reader.println("Loaded driver " + match.getDriver());
				} catch (Exception e) {
					reader.println("Unable to load driver " + match.getDriver() + ": " + e.getMessage());
				}
			}
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
				DatabaseUtil.closeResultSet(rs);
				DatabaseUtil.closeStatement(stmt);
			}
		}
		DatabaseUtil.closeConnection(conn);
	}

	private void displayResult(ResultSet rs) throws SQLException, IOException {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		reader.println("Query returned " + count + " rows");
	}

}
