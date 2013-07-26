package com.internetitem.sqshy.command;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.internetitem.sqshy.settings.Settings;

public class Commands {

	private Map<String, Constructor<? extends Command>> commandMap;
	private Settings settings;

	public Commands(Settings settings) {
		this.commandMap = new HashMap<>();
		this.settings = settings;
	}

	public void addCommand(String name, Class<? extends Command> clazz) throws Exception {
		Constructor<? extends Command> constructor = clazz.getConstructor(Settings.class);
		commandMap.put(name, constructor);
	}

	public Command getSqlCommand() {
		return new SqlCommand(settings);
	}

	public Command getCommand(String name) throws CommandException {
		Constructor<? extends Command> constructor = commandMap.get(name);
		if (constructor == null) {
			return null;
		}
		try {
			return constructor.newInstance(settings);
		} catch (Exception e) {
			throw new CommandException("Error creating command " + name + ": " + e.getMessage());
		}
	}
}
