package com.internetitem.sqshy.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseUtil {
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
