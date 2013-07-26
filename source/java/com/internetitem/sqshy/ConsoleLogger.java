package com.internetitem.sqshy;

import jline.console.ConsoleReader;

import com.internetitem.sqshy.settings.Settings;

public class ConsoleLogger implements Output {

	private Settings settings;
	private ConsoleReader reader;

	public ConsoleLogger(Settings settings, ConsoleReader reader) {
		this.settings = settings;
		this.reader = reader;
	}

	private void outputLine(String line) {
		try {
			reader.println(line);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void connectMessage(String message) {
		outputLine(message);
	}

	@Override
	public void rowCount(String message) {
		outputLine(message);
	}

	@Override
	public void error(String message) {
		outputLine(message);
	}

	@Override
	public void output(String message) {
		outputLine(message);
	}

}
