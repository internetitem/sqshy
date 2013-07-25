package com.internetitem.sqshy.config.args;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandLineState {

	private boolean firstWasSplit;
	private List<String> extras;
	private LinkedList<String> values;

	public CommandLineState(String[] args) {
		this.values = new LinkedList<>();
		this.extras = new ArrayList<>();
		boolean consumed = false;
		for (String value : args) {
			if (consumed) {
				extras.add(value);
			} else if (value.equals("--")) {
				consumed = true;
			} else {
				values.addLast(value);
			}
		}
	}

	public boolean hasNext() {
		return !values.isEmpty();
	}

	public boolean isFirstWasSplit() {
		return firstWasSplit;
	}

	public List<String> getExtras() {
		return extras;
	}

	public void insertValue(String value) {
		firstWasSplit = true;
		values.addFirst(value);
	}

	public String peek() {
		return values.peekFirst();
	}

	public String next() {
		firstWasSplit = false;
		return values.pollFirst();
	}
}
