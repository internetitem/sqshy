package com.internetitem.sqshy;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Output {

	void connectMessage(String message);

	void rowCount(String message);

	void error(String message);

	void output(String message);

	void resultSet(ResultSet rs) throws SQLException;

}
