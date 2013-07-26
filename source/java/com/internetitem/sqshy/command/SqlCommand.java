package com.internetitem.sqshy.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.DatabaseUtil;

public class SqlCommand implements Command {

	private Settings settings;
	private StringBuilder sql;
	private boolean isReady;

	public SqlCommand(Settings settings) {
		this.settings = settings;
		this.sql = new StringBuilder();
	}

	@Override
	public void addLine(String line) {
		String delimiter = settings.getDelimiter();
		String gocmd = settings.getGocmd();
		if (line.trim().endsWith(delimiter)) {
			isReady = true;
			sql.append(line.substring(0, line.lastIndexOf(delimiter)));
		} else if (line.trim().equals(gocmd)) {
			isReady = true;
		} else {
			if (sql.length() > 0) {
				sql.append("\n");
			}
			sql.append(line);
		}
	}

	@Override
	public boolean isReady() {
		return isReady;
	}

	@Override
	public void execute() throws CommandException {
		try {
			executeQuery(sql.toString());
		} catch (SQLException e) {
			throw new CommandException("Error executing SQL: " + e.getMessage());
		}
	}

	@Override
	public String getPrompt() {
		return settings.getPrompt2();
	}

	private void executeQuery(String query) throws SQLException {
		Connection conn = settings.getConnection();
		Output out = settings.getOutput();
		if (conn == null) {
			out.connectMessage("Not connected");
			return;
		}

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(query);
			do {
				rs = stmt.getResultSet();
				if (rs != null) {
					displayResult(rs);
				} else {
					int updateCount = stmt.getUpdateCount();
					out.rowCount("Affected " + updateCount + " rows");
				}
			} while (stmt.getMoreResults() || stmt.getUpdateCount() != -1);
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(stmt);
		}
	}

	private void displayResult(ResultSet rs) throws SQLException {
		Output out = settings.getOutput();
		int count = 0;
		while (rs.next()) {
			count++;
		}
		out.rowCount("Query returned " + count + " rows");
	}

}
