package com.internetitem.sqshy;

import java.io.IOException;

import jline.console.ConsoleReader;

import com.internetitem.sqshy.command.Command;
import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.command.Commands;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.DatabaseUtil;

public class SqshyRepl {

	private ConsoleReader reader;
	private Settings settings;
	private Commands commands = null;

	public SqshyRepl(ConsoleReader reader, Settings settings, Commands commands) {
		this.reader = reader;
		this.settings = settings;
		this.commands = commands;
	}

	private Command command;
	private String prompt;
	
	public void repl() throws IOException {
		String line;
		prompt = settings.getPrompt();
		while ((line = reader.readLine(prompt)) != null) {
			try {
				if (command == null) {
					command = getCommand(line);
				} else {
					command.addLine(line);
				}
				prompt = command.getPrompt();
				if (command.isReady()) {
					Command tmpCommand = command;
					reset();
					tmpCommand.execute();
				}
			} catch (CommandException e) {
				reset();
				settings.getOutput().error(e.getMessage());
			}
		}
		DatabaseUtil.closeConnection(settings.getConnection());
	}
	
	private void reset() {
		command = null;
		prompt = settings.getPrompt();
	}

	private Command getCommand(String line) throws CommandException {
		String trimmed = line.trim();
		String[] parts = trimmed.split("\\s", 2);
		String commandName = parts[0];
		String remaining = null;
		if (parts.length > 1) {
			remaining = parts[1];
		}
		Command command = commands.getCommand(commandName);
		if (command != null) {
			command.addLine(remaining);
		} else {
			command = commands.getSqlCommand();
			command.addLine(line);
		}
		return command;
	}

}
