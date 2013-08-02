package com.internetitem.sqshy.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.internetitem.sqshy.output.Output;
import com.internetitem.sqshy.settings.Settings;

public class ConnectCommand extends CommandWithArguments {

	public ConnectCommand(Settings settings) {
		super(settings);
	}

	@Override
	protected boolean interpolateNext(List<String> parameters) {
		return true;
	}

	@Override
	protected void execute(Output output, Settings settings, List<String> parameters) throws CommandException {
		if (parameters.isEmpty()) {
			throw new CommandException("Nowhere to connect");
		}
		String alias = null;
		String url = null;
		String username = null;
		String password = null;
		String driverClass = null;
		if (parameters.size() == 1) {
			String value = parameters.get(0);
			if (value.contains(":")) {
				url = value;
			} else {
				alias = value;
			}
		} else if (parameters.size() == 3) {
			url = parameters.get(0);
			username = parameters.get(1);
			password = parameters.get(2);
		} else if (parameters.size() == 4) {
			url = parameters.get(0);
			username = parameters.get(1);
			password = parameters.get(2);
			driverClass = parameters.get(3);
		} else {
			throw new CommandException("Invalid connect syntax");
		}
		Map<String, String> connectionProperties = new HashMap<>();
		settings.getConnectionManager().connect(alias, driverClass, url, username, password, connectionProperties);
	}

}
