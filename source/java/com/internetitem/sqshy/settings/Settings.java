package com.internetitem.sqshy.settings;

import com.internetitem.sqshy.ConnectionManager;
import com.internetitem.sqshy.command.CommandException;
import com.internetitem.sqshy.output.Output;
import com.internetitem.sqshy.variables.ConnectionManagerWrapper;
import com.internetitem.sqshy.variables.EnvironmentVariable;
import com.internetitem.sqshy.variables.SystemPropertyVariable;
import com.internetitem.sqshy.variables.VariableManager;

public class Settings {

	private Output originalLogger;
	private Output logger;
	private ConnectionManager connectionManager;
	private VariableManager variableManager;

	public Settings() {
		this.variableManager = new VariableManager();
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public Output getOutput() {
		return logger;
	}

	public void init(Output logger, ConnectionManager connectionManager) {
		this.logger = logger;
		this.originalLogger = logger;
		this.connectionManager = connectionManager;
		setupVariables();
	}

	private void setupVariables() {
		variableManager.setVariable("connectionManager", new ConnectionManagerWrapper(connectionManager));
		variableManager.setVariable("env", new EnvironmentVariable());
		variableManager.setVariable("sys", new SystemPropertyVariable());
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
