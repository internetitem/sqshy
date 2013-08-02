package com.internetitem.sqshy.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public static boolean parseBoolean(String stringValue) {
		String lower = stringValue.toLowerCase();
		if (lower.startsWith("f") || lower.startsWith("n") || lower.equals("0")) {
			return false;
		}
		return true;
	}

	public static Pattern variablePattern = Pattern.compile("\\$\\{([^}]+)\\}");

	public static String interpolate(String from, Map<String, String> variables) {
		Matcher matcher = variablePattern.matcher(from);
		StringBuilder builder = new StringBuilder();

		int startIndex = 0;
		while (matcher.find()) {
			builder.append(from.subSequence(startIndex, matcher.start()));
			String name = matcher.group(1);
			if (variables.containsKey(name)) {
				String value = variables.get(name);
				if (value == null) {
					value = "";
				}
				builder.append(value);
			} else {
				builder.append(matcher.group(0));
			}
			startIndex = matcher.end();
		}
		builder.append(from.substring(startIndex));

		return builder.toString();
	}

	public static String filename(String filename) {
		return filename.replaceFirst("^~/", System.getProperty("user.home") + "/");
	}
}
