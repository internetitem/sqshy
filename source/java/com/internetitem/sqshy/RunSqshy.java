package com.internetitem.sqshy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;

import com.internetitem.sqshy.command.Commands;
import com.internetitem.sqshy.command.ConnectCommand;
import com.internetitem.sqshy.config.Configuration;
import com.internetitem.sqshy.config.DatabaseConnectionConfig;
import com.internetitem.sqshy.config.DriverMatch;
import com.internetitem.sqshy.config.args.BooleanValue;
import com.internetitem.sqshy.config.args.CommandLineParseException;
import com.internetitem.sqshy.config.args.CommandLineParser;
import com.internetitem.sqshy.config.args.ListValue;
import com.internetitem.sqshy.config.args.ParsedCommandLine;
import com.internetitem.sqshy.config.args.StringValue;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.settings.SettingsSet;
import com.internetitem.sqshy.settings.SettingsSet.SettingSource;

public class RunSqshy {

	public static CommandLineParser buildCommandLineParser() {
		CommandLineParser parser = new CommandLineParser();
		parser.addArg(new BooleanValue("help", "View help message", new String[] { "--help", "-h", "-?" }));
		parser.addArg(new StringValue("connect", "Connect to saved alias\n(other connection settings override those in the alias)", new String[] { "--connect", "-c" }, false));
		parser.addArg(new StringValue("driver", "JDBC Driver Class\nIf not specified, will be guessed based on URL", new String[] { "--driver", "-d" }, false));
		parser.addArg(new StringValue("url", "JDBC URL", new String[] { "--url", "-u" }, false));
		parser.addArg(new StringValue("username", "Database Username", new String[] { "--username", "-U" }, false));
		parser.addArg(new StringValue("password", "Database Password\nIf the string @ is used, the user will be prompted", new String[] { "--password", "-P" }, false));
		parser.addArg(new ListValue("properties", "JDBC Properties (key=value)", new String[] { "--properties" }));
		parser.addArg(new StringValue("settings", "Load saved settings from file (defaults to ~/.sqshyrc)\nMissing files are ignored", new String[] { "--settings", "-s" }, false));
		parser.addArg(new ListValue("set", "Set variables", new String[] { "--set" }));
		return parser;
	}

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = buildCommandLineParser();
		ParsedCommandLine cmdline;
		try {
			cmdline = parser.parse(args);
		} catch (CommandLineParseException e) {
			System.err.println("Error: " + e.getMessage());
			System.err.println();
			System.err.println(parser.getUsageString());
			System.exit(1);
			return;
		}

		if (cmdline.getBoolValue("help")) {
			System.out.println(parser.getUsageString());
			System.exit(0);
			return;
		}

		Settings settings = new Settings();
		Configuration globalConfig = Configuration.loadFromResource("/defaults.json");
		settings.addSet(new SettingsSet(SettingSource.DefaultConfig, null, globalConfig.getVariables()));

		String settingsFilename = cmdline.getValue("settings");
		File settingsFile;
		if (settingsFilename != null) {
			settingsFile = new File(settingsFilename);
		} else {
			settingsFile = new File(System.getProperty("user.home"), ".sqshyrc");
		}

		List<DriverMatch> driverInfos = new ArrayList<>(globalConfig.getDrivers());
		List<DatabaseConnectionConfig> dcc = new ArrayList<>();

		Configuration config = null;
		if (settingsFile.isFile()) {
			String filename = settingsFile.getAbsolutePath();
			System.err.println("Loading settings file from " + filename);
			config = Configuration.loadFromFile(settingsFile);
			Map<String, String> variables = config.getVariables();
			if (variables != null) {
				settings.addSet(new SettingsSet(SettingSource.UserConfig, filename, variables));
			}
			if (config.getDrivers() != null) {
				driverInfos.addAll(config.getDrivers());
			}
			List<DatabaseConnectionConfig> connections = config.getConnections();
			if (connections != null) {
				dcc.addAll(connections);
			}
		} else {
			System.err.println("Warning: No settings file found in " + settingsFile.getAbsolutePath());
		}

		String driverClass = cmdline.getValue("driver");
		String url = cmdline.getValue("url");
		String username = cmdline.getValue("username");
		String password = cmdline.getValue("password");
		if (password != null && password.equals("@")) {
			System.out.print("Password: ");
			password = new String(System.console().readPassword());
		}
		List<String> properties = cmdline.getList("properties");
		Map<String, String> connectionProperties = listToMap(properties);
		String alias = cmdline.getValue("connect");

		List<String> variableList = cmdline.getList("set");
		if (variableList != null) {
			Map<String, String> variables = listToMap(variableList);
			if (variables != null) {
				settings.addSet(new SettingsSet(SettingSource.CommandLine, null, variables));
			}
		}

		Terminal terminal = TerminalFactory.create();
		ConsoleReader reader = new ConsoleReader("sqshy", System.in, System.out, terminal);
		ConsoleLogger logger = new ConsoleLogger(settings, reader);
		settings.init(driverInfos, dcc, logger);
		Commands commands = new Commands(settings);
		commands.addCommand("connect", ConnectCommand.class);
		if (url != null) {
			settings.connect(alias, driverClass, url, username, password, connectionProperties);
		}
		SqshyRepl repl = new SqshyRepl(reader, settings, commands);
		repl.repl();
	}

	private static Map<String, String> listToMap(List<String> properties) {
		if (properties == null || properties.isEmpty()) {
			return null;
		}
		Map<String, String> map = new HashMap<>();
		for (String s : properties) {
			String[] parts = s.split("=", 2);
			if (parts.length == 2) {
				map.put(parts[0], parts[1]);
			} else {
				map.put(s, "true");
			}
		}
		return map;
	}

}
