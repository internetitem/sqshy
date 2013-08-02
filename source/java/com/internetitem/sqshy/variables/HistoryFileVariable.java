package com.internetitem.sqshy.variables;

import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import jline.console.history.History;
import jline.console.history.MemoryHistory;

import com.internetitem.sqshy.command.CommandException;

public class HistoryFileVariable implements Variable {

	private String filename;
	private ConsoleReader reader;

	public HistoryFileVariable(ConsoleReader reader) {
		this.reader = reader;
		reader.setHistory(new MemoryHistory());
		reader.setHistoryEnabled(true);
	}

	@Override
	public String getStringValue(String fullName, String partName, String defaultValue) throws CommandException {
		if (partName == null) {
			return "<history>";
		} else if (partName.equals("file")) {
			return filename;
		} else {
			return defaultValue;
		}
	}

	@Override
	public void setStringValue(String fullName, String partName, String newValue, boolean userRequest) throws CommandException {
		if (partName == null) {
			throw new CommandException(fullName + " is readonly");
		} else if (partName.equals("file")) {
			setFile(newValue);
		} else {
			throw new CommandException(fullName + " does not exist");
		}
	}

	private void setFile(String filename) {
		if (this.filename == null || this.filename.equals("")) {
			if (filename == null || filename.equals("")) {
				return;
			}
		} else if (this.filename.equals(filename)) {
			return;
		}

		History history = reader.getHistory();
		if (history != null && history instanceof Flushable) {
			try {
				((Flushable) history).flush();
			} catch (Exception e) {
				// Ignore
			}
		}

		if (filename != null && !filename.isEmpty()) {
			try {
				history = new FileHistory(new File(filename));
			} catch (IOException e) {
				// Ignore
			}
		} else {
			history = new MemoryHistory();
		}
		this.filename = filename;
		reader.setHistory(history);
	}

	@Override
	public Set<String> getNestedNames() {
		Set<String> names = new HashSet<>();
		names.add("file");
		return names;
	}

}
