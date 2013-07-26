package com.internetitem.sqshy.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Configuration {

	private Map<String, String> variables;
	private List<DatabaseConnectionConfig> connections;
	private List<DriverMatch> drivers;

	public Configuration() {
		this.variables = new HashMap<>();
		this.connections = new ArrayList<>();
		this.drivers = new ArrayList<>();
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> Configuration) {
		this.variables = Configuration;
	}

	public List<DatabaseConnectionConfig> getConnections() {
		return connections;
	}

	public void setConnections(List<DatabaseConnectionConfig> connections) {
		this.connections = connections;
	}

	public List<DriverMatch> getDrivers() {
		return drivers;
	}

	public void setDrivers(List<DriverMatch> drivers) {
		this.drivers = drivers;
	}

	public static Configuration loadFromFile(File settingsFile) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Configuration config = mapper.readValue(settingsFile, Configuration.class);
		return config;
	}

	public static Configuration loadFromResource(String name) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Configuration config = mapper.readValue(Configuration.class.getResourceAsStream(name), Configuration.class);
		return config;
	}

}
