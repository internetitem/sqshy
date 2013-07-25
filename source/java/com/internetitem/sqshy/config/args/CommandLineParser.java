package com.internetitem.sqshy.config.args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandLineParser {

	private List<CommandLineArgument<?>> allArgs;
	private Map<String, CommandLineArgument<?>> argMap;

	public CommandLineParser() {
		this.allArgs = new ArrayList<>();
		this.argMap = new HashMap<>();
	}

	public void addArg(CommandLineArgument<?> arg) {
		this.allArgs.add(arg);
		for (String name : arg.getNames()) {
			argMap.put(name, arg);
		}
	}

	public String getUsageString() {
		StringBuilder b = new StringBuilder();

		int maxLength = 0;
		for (String arg : argMap.keySet()) {
			if (arg.length() > maxLength) {
				maxLength = arg.length();
			}
		}

		for (CommandLineArgument<?> arg : allArgs) {
			String[] lines = arg.getDescription().split("\n");
			int nameIndex = 0;
			for (; nameIndex < arg.getNames().length; nameIndex++) {
				b.append(" ");
				String argName = arg.getNames()[nameIndex];
				b.append(String.format("%1$" + maxLength + "s", argName));
				b.append(" ");
				if (nameIndex < lines.length) {
					b.append(lines[nameIndex]);
				}
				b.append("\n");
			}
			for (int lineIndex = nameIndex; lineIndex < lines.length; lineIndex++) {
				b.append(lines[lineIndex]);
				b.append("\n");
			}
		}

		return b.toString();
	}

	public ParsedCommandLine parse(String[] originalArgs) throws CommandLineParseException {
		CommandLineState args = new CommandLineState(originalArgs);

		Map<String, Object> values = new HashMap<>();
		List<String> extra = new ArrayList<>();
		while (args.hasNext()) {
			String name = args.next();
			CommandLineArgument<?> arg = argMap.get(name);
			if (arg == null) {
				if (name.contains("=")) {
					String[] parts = name.split("=", 2);
					name = parts[0];
					args.insertValue(parts[1]);
					arg = argMap.get(name);
				}
			}

			if (arg == null) {
				if (name.startsWith("-")) {
					throw new CommandLineParseException(name, "illegal argument");
				} else {
					extra.add(name);
				}
			} else {
				Object value = arg.parse(args);
				values.put(arg.getName(), value);
			}
		}
		extra.addAll(args.getExtras());
		ParsedCommandLine commandLine = new ParsedCommandLine(extra, values);
		return commandLine;
	}

	public static void parseArgs(String[] args) {
	}

}
