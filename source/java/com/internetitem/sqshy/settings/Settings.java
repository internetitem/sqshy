package com.internetitem.sqshy.settings;

import java.util.Map;

import jline.console.ConsoleReader;

import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.connection.ConnectionManager;
import com.internetitem.sqshy.output.Output;
import com.internetitem.sqshy.variables.ConnectionManagerWrapper;
import com.internetitem.sqshy.variables.EnvironmentVariable;
import com.internetitem.sqshy.variables.HistoryFileVariable;
import com.internetitem.sqshy.variables.SystemPropertyVariable;
import com.internetitem.sqshy.variables.VariableManager;

public class Settings {

	private Output originalLogger;
	private Output logger;
	private ConnectionManager connectionManager;
	private VariableManager variableManager;
	private ConsoleReader reader;

	public Settings() {
		this.variableManager = new VariableManager();
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public Output getOutput() {
		return logger;
	}

	public void init(ConsoleReader reader, Output logger, ConnectionManager connectionManager, Map<String, String> initialVariables) throws CommandException {
		this.reader = reader;
		this.logger = logger;
		this.originalLogger = logger;
		this.connectionManager = connectionManager;
		setupVariables(initialVariables);
	}

	private void setupVariables(Map<String, String> vars) throws CommandException {
		variableManager.setVariable("connectionManager", new ConnectionManagerWrapper(connectionManager));
		variableManager.setVariable("env", new EnvironmentVariable());
		variableManager.setVariable("sys", new SystemPropertyVariable());
		variableManager.setVariable("history", new HistoryFileVariable(reader));
		variableManager.addAll(vars);
	}

	public VariableManager getVariableManager() {
		return variableManager;
	}

	public String getPrompt() throws CommandException {
		return getVariableManager().getValue("prompt", "sql> ");
	}

	public String getPrompt2() throws CommandException {
		return getVariableManager().getValue("prompt2", "> ");
	}

	public String getGocmd() throws CommandException {
		return getVariableManager().getValue("gocmd", "\\go");
	}

	public String getDelimiter() throws CommandException {
		return getVariableManager().getValue("delimiter", ";");
	}

}
