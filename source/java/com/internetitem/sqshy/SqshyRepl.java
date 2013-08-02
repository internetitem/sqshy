package com.internetitem.sqshy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jline.console.ConsoleReader;

import com.internetitem.sqshy.command.Command;
import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.command.Commands;
import com.internetitem.sqshy.output.CloseableOutput;
import com.internetitem.sqshy.output.FileOutput;
import com.internetitem.sqshy.output.PipeOutput;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.StringConsumer;

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

	public void repl() throws IOException {

		while (true) {
			String prompt;
			try {
				if (command != null) {
					prompt = command.getPrompt();
				} else {
					prompt = settings.getPrompt();
				}
			} catch (CommandException e) {
				prompt = "sql> "; // This can't really happen
			}
			String line = reader.readLine(prompt);
			if (line == null) {
				break;
			}

			StringConsumer consumer = new StringConsumer(line);
			try {
				boolean atStart = true;
				while (consumer.hasMore()) {
					if (command == null) {
						atStart = true;
						consumer.consumeWhitespace();
						if (!consumer.hasMore()) {
							continue;
						}
						String commandName = consumer.findReAtStart("\\\\?\\w+\\b");
						if (commandName != null) {
							command = commands.getCommand(commandName);
							if (command != null) {
								consumer.consumeLastFind();
							}
						}

						if (command == null) {
							command = commands.getSqlCommand();
						}
					} else {
						command.consume(atStart, consumer);
						atStart = false;
					}

					if (command.couldBeDone()) {
						consumer.consumeWhitespace();
						String op = consumer.findReAtStart("(\\||>>?)");
						if (op != null) {
							consumer.consumeLastFind();

							String errorMessage = null;
							try {
								CloseableOutput out = null;
								if (op.startsWith(">")) {
									String outfile = consumer.consumeArg(true, settings);
									if (outfile == null) {
										throw new CommandException("missing or invalid filename");
									}
									errorMessage = "Error writing to file " + outfile;
									out = new FileOutput(settings, outfile, op.equals(">>"));
								} else if (op.equals("|")) {
									String executable = consumer.consumeArg(true, settings);
									List<String> arguments = new ArrayList<>();
									if (executable == null) {
										throw new CommandException("missing or invalid executable");
									}
									arguments.add(executable);
									while (consumer.hasMore()) {
										arguments.add(consumer.consumeArg(true, settings));
									}
									errorMessage = "Error piping to " + executable + ":";
									out = new PipeOutput(settings, arguments);
								}

								if (out != null) {
									command.execute(out);
									out.close();
								}
							} catch (IOException e) {
								throw new CommandException(errorMessage + ": " + e.getMessage());
							}

							command = null;
							continue;
						}

						if (command.isDone() || (!consumer.hasMore() && !command.isMultiline())) {
							command.execute(settings.getOutput());
							command = null;
							continue;
						}
					}
				}

				if (command != null) {
					command.newline();
				}
			} catch (CommandException e) {
				command = null;
				settings.getOutput().error(e.getMessage());
			}
		}
		settings.getConnectionManager().closeConnection(true);
	}
}
