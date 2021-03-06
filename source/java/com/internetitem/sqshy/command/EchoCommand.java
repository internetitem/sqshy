package com.internetitem.sqshy.command;

import java.util.List;

import com.internetitem.sqshy.output.Output;
import com.internetitem.sqshy.settings.Settings;

public class EchoCommand extends CommandWithArguments {

	public EchoCommand(Settings settings) {
		super(settings);
	}

	@Override
	protected boolean interpolateNext(List<String> parameters) {
		return true;
	}

	@Override
	protected void execute(Output output, Settings settings, List<String> parameters) throws CommandException {
		StringBuilder out = new StringBuilder();
		for (String value : parameters) {
			if (out.length() > 0) {
				out.append(" ");
			}
			out.append(value);
		}
		output.output(out.toString());
	}

}
