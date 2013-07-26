package com.internetitem.sqshy.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.StringUtil;

public class SetCommand extends CommandWithArguments {

	public SetCommand(Settings settings) {
		super(settings);
	}

	@Override
	protected void execute(Settings settings, List<String> parameters) throws CommandException {
		Output output = settings.getOutput();
		if (parameters.size() == 0) {
			Map<String, String> variables = settings.getVariables();
			List<String> keys = new ArrayList<>(variables.keySet());
			Collections.sort(keys);
			for (String key : keys) {
				String value = variables.get(key);
				if (value == null) {
					value = "<unset>";
				}
				output.output(key + "=" + value);
			}
		} else if (parameters.size() == 1) {
			String name = parameters.get(0);
			if (settings.hasValue(name)) {
				String value = settings.getStringValue(name);
				if (value == null) {
					value = "<unset>";
				}
				output.output(name + "=" + value);
			} else {
				output.output(name + " is not set");
			}
		} else if (parameters.size() == 2) {
			String name = parameters.get(0);
			String value = parameters.get(1);
			String realValue = StringUtil.interpolate(value, settings.getVariables());
			settings.setValue(name, realValue);
		} else {
			throw new CommandException("wrong number of arguments to set");
		}
	}

}
