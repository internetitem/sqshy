package com.internetitem.sqshy.command;

import java.util.List;

import com.internetitem.sqshy.output.Output;
import com.internetitem.sqshy.settings.Settings;

public class ReconnectCommand extends CommandWithArguments {

	public ReconnectCommand(Settings settings) {
		super(settings);
	}

	@Override
	protected boolean interpolateNext(List<String> parameters) {
		return false;
	}

	@Override
	protected void execute(Output output, Settings settings, List<String> parameters) throws CommandException {
		if (!parameters.isEmpty()) {
			throw new CommandException("too many arguments");
		}
		settings.getConnectionManager().reconnect();
	}

}
