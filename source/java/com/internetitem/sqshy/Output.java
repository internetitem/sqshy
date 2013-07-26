package com.internetitem.sqshy;

public interface Output {

	void connectMessage(String message);

	void rowCount(String message);

	void error(String message);

}
