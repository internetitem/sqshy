package com.internetitem.sqshy.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.internetitem.sqshy.output.Output;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.variables.Variable;
import com.internetitem.sqshy.variables.VariableManager;

public class SetCommand extends CommandWithArguments {

	public SetCommand(Settings settings) {
		super(settings);
	}

	@Override
	protected boolean interpolateNext(List<String> parameters) {
		if (parameters.size() >= 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void execute(Output output, Settings settings, List<String> parameters) throws CommandException {
		VariableManager variableManager = settings.getVariableManager();
		if (parameters.size() == 0) {
			List<String> keys = variableManager.getVariableNames();
			Collections.sort(keys);
			for (String key : keys) {
				String value = variableManager.getValue(key, null);
				if (value == null) {
					value = "<unset>";
				}
				output.output(key + "=" + value);
			}
		} else if (parameters.size() == 1) {
			String name = parameters.get(0);
			Variable variable = variableManager.getVariable(name);
			Set<String> names = null;
			if (variable != null) {
				names = variable.getNestedNames();
			}

			if (names != null) {
				List<String> allNames = new ArrayList<>(names);
				Collections.sort(allNames);
				for (String varName : allNames) {
					String fullName = name + "." + varName;
					output.output(fullName + "=" + variable.getStringValue(fullName, varName, "<unset>"));
				}
			} else {
				String value = variableManager.getValue(name, null);
				if (value == null) {
					value = "<unset>";
				}
				output.output(name + "=" + value);
			}
		} else if (parameters.size() == 2) {
			String name = parameters.get(0);
			String value = parameters.get(1);
			variableManager.setValue(name, value, true);
		} else {
			throw new CommandException("wrong number of arguments to set");
		}
	}

}
