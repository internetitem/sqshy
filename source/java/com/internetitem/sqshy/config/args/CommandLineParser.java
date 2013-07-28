package com.internetitem.sqshy.config.args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.internetitem.sqshy.config.args.CommandLineArgument.ArgumentType;

public class CommandLineParser {

	private List<CommandLineArgument> allArgs;
	private Map<String, CommandLineArgument> longArgMap;
	private Map<String, CommandLineArgument> shortArgMap;

	public CommandLineParser() {
		this.allArgs = new ArrayList<>();
		this.longArgMap = new HashMap<>();
		this.shortArgMap = new HashMap<>();
	}

	public void addArg(String name, String longName, String shortName, ArgumentType argumentType, String description) {
		if (shortName != null && shortName.length() > 1) {
			throw new RuntimeException("shortname must be one character");
		}

		CommandLineArgument arg = new CommandLineArgument(name, shortName, longName, argumentType, description);
		this.allArgs.add(arg);
		if (shortName != null) {
			if (shortArgMap.containsKey(shortName)) {
				throw new RuntimeException("duplicate argument -" + shortName);
			}
			shortArgMap.put(shortName, arg);
		}
		if (longName != null) {
			if (longArgMap.containsKey(longName)) {
				throw new RuntimeException("duplicate argument --" + shortName);
			}
			longArgMap.put(longName, arg);
		}

	}

	public String getUsageString() {
		StringBuilder b = new StringBuilder();

		int maxLength = 0;
		for (CommandLineArgument arg : allArgs) {
			String name = arg.getLongName();
			if (name != null && name.length() > maxLength) {
				maxLength = name.length();
			}
		}

		String formatString = "%1$" + (maxLength + 2) + "s";
		for (CommandLineArgument arg : allArgs) {
			String[] lines = arg.getDescription().split("\n");
			String longName = arg.getLongName();
			String shortName = arg.getShortName();
			int lineIndex = 0;
			if (longName != null) {
				b.append(" ");
				b.append(String.format(formatString, "--" + longName));
				b.append(" ");
				if (lineIndex < lines.length) {
					b.append(lines[lineIndex]);
				}
				b.append("\n");
				lineIndex++;
			}
			if (shortName != null) {
				b.append(" ");
				b.append(String.format(formatString, "-" + shortName));
				b.append(" ");
				if (lineIndex < lines.length) {
					b.append(lines[lineIndex]);
				}
				b.append("\n");
				lineIndex++;
			}
			for (; lineIndex < lines.length; lineIndex++) {
				for (int i = 0; i < maxLength + 4; i++) {
					b.append(" ");
				}
				b.append(lines[lineIndex]);
				b.append("\n");
			}
		}

		return b.toString();
	}

	public ParsedCommandLine parse(String[] originalArgs) throws CommandLineParseException {
		LinkedList<String> args = new LinkedList<>();
		for (String arg : originalArgs) {
			args.addLast(arg);
		}

		List<String> extraArgs = new ArrayList<>();
		Map<String, String> stringValues = new HashMap<>();
		Map<String, List<String>> listValues = new HashMap<>();
		Set<String> foundArgs = new HashSet<>();

		boolean foundDashDash = false;
		CommandLineArgument lastArg = null;
		while (!args.isEmpty()) {
			String value = args.removeFirst();
			if (foundDashDash) {
				if (lastArg != null && lastArg.getArgumentType() == ArgumentType.List) {
					addToList(lastArg, value, listValues);
				} else {
					extraArgs.add(value);
				}
			} else if (value.equals("--")) {
				foundDashDash = true;
			} else {
				if (value.startsWith("--")) {
					String name = value.substring(2);
					String argValue = null;
					if (name.contains("=")) {
						String[] parts = name.split("=", 2);
						name = parts[0];
						argValue = parts[1];
					}
					CommandLineArgument arg = longArgMap.get(name);
					if (arg == null) {
						throw new CommandLineParseException("--" + name, "unknown argument");
					} else if (foundArgs.contains(arg.getName()) && arg.getArgumentType() != ArgumentType.List) {
						throw new CommandLineParseException("--" + name, "already specified");
					} else {
						String argName = arg.getName();
						lastArg = arg;
						foundArgs.add(argName);
						switch (arg.getArgumentType()) {
						case List:
						case OptionalArg:
						case RequiredArg:
							if (argValue == null && !args.isEmpty()) {
								argValue = args.removeFirst();
							}
							break;
						case NoArg:
							if (argValue != null) {
								throw new CommandLineParseException("--" + name, "does not take an argument");
							}
						}
						switch (arg.getArgumentType()) {
						case List:
						case RequiredArg:
							if (argValue == null) {
								throw new CommandLineParseException("--" + name, "missing required value");
							}
						case NoArg:
						case OptionalArg:
						}
						switch (arg.getArgumentType()) {
						case NoArg:
							argValue = "true";
						case RequiredArg:
						case OptionalArg:
							stringValues.put(argName, argValue);
							break;
						case List:
							addToList(arg, argValue, listValues);
							break;
						}
					}
				} else if (value.startsWith("-")) {
					String nameString = value.substring(1);
					for (int i = 0; i < nameString.length(); i++) {
						String name = nameString.substring(i, i + 1);
						CommandLineArgument arg = shortArgMap.get(name);
						if (arg == null) {
							throw new CommandLineParseException("-" + name, "unknown argument");
						} else if (nameString.length() > 1 && arg.getArgumentType() != ArgumentType.NoArg) {
							throw new CommandLineParseException("-" + name, "can't be combined with other arguments");
						} else if (foundArgs.contains(arg.getName()) && arg.getArgumentType() != ArgumentType.List) {
							throw new CommandLineParseException("-" + name, "already specified");
						} else {
							String argName = arg.getName();
							foundArgs.add(argName);
							lastArg = arg;
							String argValue = null;

							switch (arg.getArgumentType()) {
							case List:
							case OptionalArg:
							case RequiredArg:
								if (!args.isEmpty()) {
									argValue = args.removeFirst();
								}
							case NoArg:
							}
							switch (arg.getArgumentType()) {
							case List:
							case RequiredArg:
								if (argValue == null) {
									throw new CommandLineParseException("-" + name, "missing required value");
								}
							case NoArg:
							case OptionalArg:
							}
							switch (arg.getArgumentType()) {
							case NoArg:
								argValue = "true";
							case RequiredArg:
							case OptionalArg:
								stringValues.put(argName, argValue);
								break;
							case List:
								addToList(arg, argValue, listValues);
								break;
							}

						}
					}
				} else if (lastArg != null && lastArg.getArgumentType() == ArgumentType.List) {
					addToList(lastArg, value, listValues);
				} else {
					extraArgs.add(value);
				}
			}
		}

		ParsedCommandLine commandLine = new ParsedCommandLine(extraArgs, stringValues, listValues);
		return commandLine;
	}

	private void addToList(CommandLineArgument arg, String value, Map<String, List<String>> listValues) {
		String argName = arg.getName();
		List<String> values = listValues.get(argName);
		if (values == null) {
			values = new ArrayList<>();
			listValues.put(argName, values);
		}
		values.add(value);
	}

}
