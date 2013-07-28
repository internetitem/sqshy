package com.internetitem.sqshy;

import java.util.HashSet;
import java.util.Set;

import com.internetitem.sqshy.command.CommandException;

public class ConnectionManagerWrapper implements Variable {

	private ConnectionManager connectionManager;

	public ConnectionManagerWrapper(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public String getStringValue(String fullName, String partName, String defaultValue) throws CommandException {
		if (partName == null) {
			return "ConnectionManager";
		} else if (partName.equals("username")) {
			return connectionManager.getUsername();
		} else if (partName.equals("password")) {
			return connectionManager.getPassword();
		} else if (partName.equals("url")) {
			return connectionManager.getUrl();
		} else if (partName.equals("alias")) {
			return connectionManager.getAlias();
		} else if (partName.equals("driverClass")) {
			return connectionManager.getDriverClass();
		} else if (partName.equals("connected")) {
			return new Boolean(connectionManager.getConnection() != null).toString();
		} else {
			throw new CommandException(fullName + " does not have property " + partName);
		}
	}

	@Override
	public void setStringValue(String fullName, String partName, String newValue, boolean userRequest) throws CommandException {
		throw new CommandException(fullName + " is read-only");
	}

	@Override
	public Set<String> getNestedNames() {
		Set<String> names = new HashSet<>();
		maybeAdd(names, connectionManager.getUsername(), "username");
		maybeAdd(names, connectionManager.getPassword(), "password");
		maybeAdd(names, connectionManager.getUrl(), "url");
		maybeAdd(names, connectionManager.getAlias(), "alias");
		maybeAdd(names, connectionManager.getDriverClass(), "driverClass");
		names.add("connected");
		return names;
	}

	private void maybeAdd(Set<String> names, String value, String name) {
		if (value != null) {
			names.add(name);
		}
	}

}
