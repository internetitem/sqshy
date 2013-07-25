package com.internetitem.sqshy.config.args;

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
				return parseValue(args.next());
			}
		}
		return Boolean.TRUE;
	}

	private Boolean parseValue(String stringValue) {
		String lower = stringValue.toLowerCase();
		if (lower.startsWith("f") || lower.startsWith("n") || lower.equals("0")) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
