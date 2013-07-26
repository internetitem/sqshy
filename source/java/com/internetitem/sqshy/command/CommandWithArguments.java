package com.internetitem.sqshy.command;

import java.util.ArrayList;
import java.util.List;

import com.internetitem.sqshy.settings.Settings;

public abstract class CommandWithArguments implements Command {

	private Settings settings;
	private List<String> parameters;

	public CommandWithArguments(Settings settings) {
		this.settings = settings;
		this.parameters = new ArrayList<>();
	}

	@Override
	public void addLine(String line) throws CommandException {
		if (line == null) {
			return;
		}

		char quoteChar = 0;
		StringBuilder tokenBuilder = null;
		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '\\') {
				if (tokenBuilder == null) {
					tokenBuilder = new StringBuilder();
				}
				if (quoteChar == 0) {
					if (i + 1 >= line.length()) {
						tokenBuilder.append(ch);
					} else {
						char nextChar = line.charAt(++i);
						if (nextChar == '"' || nextChar == '\'') {
							tokenBuilder.append(nextChar);
						} else {
							tokenBuilder.append('\\');
							tokenBuilder.append(nextChar);
						}
					}
				} else {
					if (i + 1 >= line.length()) {
						throw new CommandException("trailing backslash");
					}
					char nextChar = line.charAt(++i);
					if (nextChar == quoteChar) {
						tokenBuilder.append(nextChar);
					} else {
						switch (nextChar) {
						case 'n':
							tokenBuilder.append('\n');
							break;
						case 't':
							tokenBuilder.append('\t');
							break;
						default:
							tokenBuilder.append('\\');
							tokenBuilder.append(nextChar);
						}
					}
				}
			} else if (quoteChar != 0) {
				if (ch == quoteChar) {
					quoteChar = 0;
				} else {
					tokenBuilder.append(ch);
				}
			} else if (ch == '\'' || ch == '"') {
				if (tokenBuilder == null) {
					tokenBuilder = new StringBuilder();
				}
				quoteChar = ch;
			} else if (ch == ' ' || ch == '\t') {
				while (i + 1 < line.length()) {
					char nextChar = line.charAt(i + 1);
					if (nextChar == ' ' || nextChar == '\t') {
						i++;
					} else {
						break;
					}
				}
				if (tokenBuilder != null) {
					parameters.add(tokenBuilder.toString());
					tokenBuilder = null;
				}
			} else {
				if (tokenBuilder == null) {
					tokenBuilder = new StringBuilder();
				}
				tokenBuilder.append(ch);
			}
		}
		if (tokenBuilder != null) {
			parameters.add(tokenBuilder.toString());
		}
		if (quoteChar != 0) {
			throw new CommandException("unclosed " + quoteChar + " quote");
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void execute() throws CommandException {
		execute(settings, parameters);

	}

	protected abstract void execute(Settings settings, List<String> parameters) throws CommandException;

	@Override
	public String getPrompt() {
		return null;
	}

}
