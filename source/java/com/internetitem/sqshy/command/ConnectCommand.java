package com.internetitem.sqshy.command;

import java.util.HashMap;
import java.util.Map;

import com.internetitem.sqshy.settings.Settings;

public class ConnectCommand implements Command {

	private Settings settings;
	private String[] parts;

	public ConnectCommand(Settings settings) {
		this.settings = settings;
	}

	@Override
	public void addLine(String line) {
		this.parts = line.split("\\s+");
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void execute() throws CommandException {
		if (parts.length == 0) {
			throw new CommandException("Nowhere to connect");
		}
		String alias = null;
		String url = null;
		String username = null;
		String password = null;
		String driverClass = null;
		if (parts.length == 1) {
			String value = parts[0];
			if (value.contains(":")) {
				url = value;
			} else {
				alias = value;
			}
		} else if (parts.length == 3) {
			url = parts[0];
			username = parts[1];
			password = parts[2];
		} else if (parts.length == 4) {
			url = parts[0];
			username = parts[1];
			password = parts[2];
			driverClass = parts[3];
		} else {
			throw new CommandException("Invalid connect syntax");
		}
		Map<String, String> connectionProperties = new HashMap<>();
		settings.connect(alias, driverClass, url, username, password, connectionProperties);
	}

	@Override
	public String getPrompt() {
		return null;
	}

}
