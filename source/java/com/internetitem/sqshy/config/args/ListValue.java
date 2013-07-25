package com.internetitem.sqshy.config.args;

import java.util.ArrayList;
import java.util.List;

public class ListValue extends CommandLineArgument<List<String>> {

	public ListValue(String name, String description, String[] names) {
		super(name, description, names);
	}

	@Override
	protected List<String> parse(CommandLineState args) throws CommandLineParseException {
		List<String> values = new ArrayList<>();
		boolean firstWasSplit = args.isFirstWasSplit();
		String endString = "//";
		if (firstWasSplit) {
			endString = args.next();
		}
		while (args.hasNext()) {
			String value = args.next();
			if (value.equals(endString)) {
				break;
			} else {
				values.add(value);
			}
		}
		return values;
	}

}
