package com.internetitem.sqshy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;

import com.internetitem.sqshy.config.Configuration;
import com.internetitem.sqshy.config.DatabaseConnectionConfig;
import com.internetitem.sqshy.config.DriverMatch;
import com.internetitem.sqshy.config.args.BooleanValue;
import com.internetitem.sqshy.config.args.CommandLineParseException;
import com.internetitem.sqshy.config.args.CommandLineParser;
import com.internetitem.sqshy.config.args.ListValue;
import com.internetitem.sqshy.config.args.ParsedCommandLine;
import com.internetitem.sqshy.config.args.StringValue;

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

		Configuration globalConfig = Configuration.loadFromResource("/defaults.json");

		String settingsFilename = cmdline.getValue("settings");
		File settingsFile;
		if (settingsFilename != null) {
			settingsFile = new File(settingsFilename);
		} else {
			settingsFile = new File(System.getProperty("user.home"), ".sqshyrc");
		}

		Configuration config = null;
		if (settingsFile.isFile()) {
			System.err.println("Loading settings file from " + settingsFile.getAbsolutePath());
			config = Configuration.loadFromFile(settingsFile);
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
		Map<String, String> connectionProperties = null;
		if (!properties.isEmpty()) {
			connectionProperties = new HashMap<>();
			for (String s : properties) {
				String[] parts = s.split("=", 2);
				if (parts.length == 2) {
					connectionProperties.put(parts[0], parts[1]);
				} else {
					connectionProperties.put(s, "true");
				}
			}
		}

		Map<String, String> variables = new HashMap<>();

		String alias = cmdline.getValue("connect");
		OUTER: if (alias != null) {
			if (config == null) {
				System.err.println("Warning: connect specified but no configuration loaded");
			} else {
				for (DatabaseConnectionConfig dcc : config.getConnections()) {
					if (alias.equals(dcc.getAlias())) {
						if (driverClass == null) {
							driverClass = dcc.getDriverClass();
						}
						if (url == null) {
							url = dcc.getUrl();
						}
						if (username == null) {
							username = dcc.getUsername();
						}
						if (password == null) {
							password = dcc.getPassword();
						}
						if (properties == null) {
							connectionProperties = dcc.getProperties();
						}
						putIfAbsent(variables, dcc.getVariables());
						break OUTER;
					}
				}
				System.err.println("Warning: connect specified but alias not found");
			}
		}

		List<DriverMatch> driverInfos = new ArrayList<>(globalConfig.getDrivers());
		if (config != null) {
			putIfAbsent(variables, config.getVariables());
			if (config.getDrivers() != null) {
				driverInfos.addAll(config.getDrivers());
			}
		}
		putIfAbsent(variables, globalConfig.getVariables());

		Terminal terminal = TerminalFactory.create();
		ConsoleReader reader = new ConsoleReader("sqshy", System.in, System.out, terminal);
		SqshyRepl repl = new SqshyRepl(reader, variables, driverInfos);
		if (url != null) {
			repl.connect(driverClass, url, username, password, connectionProperties);
		}
		repl.start();
	}

	public static void putIfAbsent(Map<String, String> to, Map<String, String> from) {
		if (from == null) {
			return;
		}
		for (Entry<String, String> e : from.entrySet()) {
			if (!to.containsKey(e.getKey())) {
				to.put(e.getKey(), e.getValue());
			}
		}
	}
}
