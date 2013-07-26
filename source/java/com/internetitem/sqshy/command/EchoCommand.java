package com.internetitem.sqshy.command;

import java.util.List;
import java.util.Map;

import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.StringUtil;

public class EchoCommand extends CommandWithArguments {

	public EchoCommand(Settings settings) {
		super(settings);
	}

	@Override
	protected void execute(Settings settings, List<String> parameters) throws CommandException {
		Output output = settings.getOutput();
		Map<String, String> variables = settings.getVariables();
		StringBuilder out = new StringBuilder();
		for (String value : parameters) {
			String interpolated = StringUtil.interpolate(value, variables);
			if (out.length() > 0) {
				out.append(" ");
			}
			out.append(interpolated);
		}
		output.output(out.toString());
	}

}
