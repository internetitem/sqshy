package com.internetitem.sqshy.util;

public class StringUtil {
	public static boolean parseBoolean(String stringValue) {
		String lower = stringValue.toLowerCase();
		if (lower.startsWith("f") || lower.startsWith("n") || lower.equals("0")) {
			return false;
		}
		return true;
	}
}
