package com.internetitem.sqshy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;

import com.internetitem.sqshy.config.DatabaseConnectionConfig;
import com.internetitem.sqshy.config.args.BooleanValue;
import com.internetitem.sqshy.config.args.CommandLineParseException;
import com.internetitem.sqshy.config.args.CommandLineParser;
import com.internetitem.sqshy.config.args.ListValue;
import com.internetitem.sqshy.config.args.ParsedCommandLine;
import com.internetitem.sqshy.config.args.StringValue;

public class RunSqshy {

	public static void main(String[] args) throws IOException {
		CommandLineParser parser = new CommandLineParser();
		parser.addArg(new BooleanValue("help", "View help message", new String[] { "--help", "-h", "-?" }));
		parser.addArg(new StringValue("driver", "JDBC Driver Class\nIf not specified, will be guessed based on URL", new String[] { "--driver", "-d" }, true));
		parser.addArg(new StringValue("url", "JDBC URL", new String[] { "--url", "-u" }, true));
		parser.addArg(new StringValue("username", "Database Username", new String[] { "--username", "-u" }, true));
		parser.addArg(new StringValue("password", "Database Password\nIf the string @ is used, the user will be prompted", new String[] { "--password", "-p" }, true));
		parser.addArg(new ListValue("properties", "JDBC Properties (key=value)", new String[] { "--properties" }));
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

		String driverClass = cmdline.getValue("driver");
		String url = cmdline.getValue("url");
		String username = cmdline.getValue("username");
		String password = cmdline.getValue("password");
		if (password != null && password.equals("@")) {
			System.out.print("Password: ");
			password = new String(System.console().readPassword());
		}
		System.err.println("password [" + password + "]");
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
		DatabaseConnectionConfig config = new DatabaseConnectionConfig(driverClass, url, username, password, connectionProperties);
		Terminal terminal = TerminalFactory.create();
		ConsoleReader reader = new ConsoleReader("sqshy", System.in, System.out, terminal);
		String line;
		while ((line = reader.readLine("sql> ")) != null) {
			reader.println("You said [" + line + "]");
		}
	}
}
