package com.internetitem.sqshy.config.args;

import com.internetitem.sqshy.util.StringUtil;

public class BooleanValue extends CommandLineArgument<Boolean> {

	public BooleanValue(String name, String description, String[] names) {
		super(name, description, names);
	}

	@Override
	protected Boolean parse(CommandLineState args) throws CommandLineParseException {
		boolean firstWasSplit = args.isFirstWasSplit();
		String value = args.peek();
		if (value != null) {
			if (firstWasSplit || !value.startsWith("-")) {
				return Boolean.valueOf(StringUtil.parseBoolean(args.next()));
			}
		}
		return Boolean.TRUE;
	}

}
