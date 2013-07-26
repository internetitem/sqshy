package com.internetitem.sqshy.command;

public interface Command {

	void addLine(String line) throws CommandException;

	boolean isReady();

	void execute() throws CommandException;

	String getPrompt();

}
