package com.internetitem.sqshy.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.DatabaseUtil;
import com.internetitem.sqshy.util.StringConsumer;

public class SqlCommand implements Command {

	private Settings settings;
	private StringBuilder sql;
	private boolean isDone;

	public SqlCommand(Settings settings) {
		this.settings = settings;
		this.sql = new StringBuilder();
	}

	@Override
	public void consume(boolean first, StringConsumer consumer) throws CommandException {
		String delimiter = settings.getDelimiter();
		String gocmd = settings.getGocmd();

		StringBuilder b = new StringBuilder();
		while (consumer.hasMore()) {
			b.append(consumer.consume());
			String asString = b.toString();
			if (asString.endsWith(delimiter)) {
				sql.append(b.substring(0, b.length() - delimiter.length()));
				isDone = true;
				return;
			} else if (asString.startsWith(gocmd) && (!consumer.hasMore() || consumer.consumeWhitespace())) {
				isDone = true;
				return;
			}
		}
		sql.append(b);
	}

	@Override
	public void newline() {
		sql.append("\n");
	}

	@Override
	public void execute(Output output) throws CommandException {
		try {
			executeQuery(output, settings.getConnectionManager().getConnection(), sql.toString());
		} catch (SQLException e) {
			throw new CommandException("Error executing SQL: " + e.getMessage());
		}
	}

	private void executeQuery(Output out, Connection conn, String query) throws SQLException {
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
					out.resultSet(rs);
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

	@Override
	public boolean couldBeDone() {
		return isDone;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public boolean isMultiline() {
		return true;
	}

	@Override
	public String getPrompt() throws CommandException {
		return settings.getPrompt2();
	}

}
