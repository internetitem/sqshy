package com.internetitem.sqshy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.settings.Settings;

public class StringConsumer {
	private int lastFind;
	private int index;
	private String string;

	public StringConsumer(String string) {
		this.string = string;
	}

	public boolean hasMore() {
		return index < string.length();
	}

	public char consume() {
		return string.charAt(index++);
	}

	public char peek() {
		return string.charAt(index);
	}

	public void consumeLastFind() {
		index = lastFind;
	}

	public String findStringAtStart(String string) {
		return findReAtStart(Pattern.quote(string));
	}

	public String findReAtStart(String re) {
		Matcher matcher = Pattern.compile("^" + re).matcher(string.substring(index));
		if (matcher.find()) {
			String value = matcher.group();
			lastFind = index + matcher.end();
			return value;
		} else {
			return null;
		}
	}

	public boolean consumeWhitespace() {
		boolean consumed = false;
		while (hasMore()) {
			char ch = peek();
			if (ch == ' ' || ch == '\t') {
				consume();
				consumed = true;
			} else {
				break;
			}
		}
		return consumed;
	}

	public String consumeArg(boolean interpolate, Settings settings) throws CommandException {
		consumeWhitespace();

		boolean consumed = false;
		char quoteChar = 0;
		StringBuilder variableBuilder = null;
		StringBuilder tokenBuilder = new StringBuilder();
		while (hasMore()) {
			char ch = consume();
			if (variableBuilder != null) {
				if (ch == '}') {
					String value = settings.getVariableManager().getValue(variableBuilder.toString(), null);
					if (value == null) {
						value = "";
					}
					tokenBuilder.append(value);
					variableBuilder = null;
				} else {
					variableBuilder.append(ch);
				}
			} else if (ch == '\\') {
				consumed = true;
				if (!hasMore()) {
					throw new CommandException("trailing backslash");
				}
				char nextChar = consume();

				if (quoteChar == 0) {
					if (nextChar == '"' || nextChar == '\'') {
						tokenBuilder.append(nextChar);
					} else {
						tokenBuilder.append('\\');
						tokenBuilder.append(nextChar);
					}
				} else {
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
						case '$':
							if (interpolate) {
								tokenBuilder.append('$');
							} else {
								tokenBuilder.append("\\$");
							}
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
					if (interpolate && quoteChar == '"' && ch == '$' && hasMore() && peek() == '{') {
						consume();
						variableBuilder = new StringBuilder();
					} else {
						tokenBuilder.append(ch);
					}
				}
			} else if (ch == '\'' || ch == '"') {
				consumed = true;
				quoteChar = ch;
			} else if (ch == ' ' || ch == '\t') {
				break;
			} else {
				consumed = true;
				if (interpolate && ch == '$' && hasMore() && peek() == '{') {
					consume();
					variableBuilder = new StringBuilder();
				} else {
					tokenBuilder.append(ch);
				}
			}
		}

		if (variableBuilder != null) {
			throw new CommandException("unclosed variable reference");
		}
		if (quoteChar != 0) {
			throw new CommandException("unclosed " + quoteChar + " quote");
		}

		if (!consumed) {
			return null;
		}

		return tokenBuilder.toString();
	}
}
