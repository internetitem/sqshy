package com.internetitem.sqshy.command;

import com.internetitem.sqshy.Output;
import com.internetitem.sqshy.util.StringConsumer;

public interface Command {

	void consume(boolean first, StringConsumer consumer) throws CommandException;

	void execute(Output output) throws CommandException;

	String getPrompt();

	boolean couldBeDone();

	boolean isDone();

	boolean isMultiline();

	void newline();

}
