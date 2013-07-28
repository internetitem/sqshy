package com.internetitem.sqshy;

import jline.console.ConsoleReader;

import com.internetitem.sqshy.settings.Settings;

public class ConsoleLogger extends AbstractOutput {

	private ConsoleReader reader;

	public ConsoleLogger(Settings settings, ConsoleReader reader) {
		super(settings);
		this.reader = reader;
	}

	@Override
	public void outputLine(String line) {
		try {
			reader.println(line);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
