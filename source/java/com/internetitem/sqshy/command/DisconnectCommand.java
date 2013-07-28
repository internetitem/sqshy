package com.internetitem.sqshy.command;

import java.util.List;

import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.settings.Settings;

public class DisconnectCommand extends CommandWithArguments {

	public DisconnectCommand(Settings settings) {
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
		settings.getConnectionManager().disconnect();
	}

}
