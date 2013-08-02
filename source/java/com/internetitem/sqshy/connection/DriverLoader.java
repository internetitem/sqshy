package com.internetitem.sqshy.connection;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.internetitem.sqshy.config.DriverMatch;
import com.internetitem.sqshy.settings.Settings;

public class DriverLoader {

	private Set<String> loadedDrivers;
	private URLClassLoader classLoader;
	private Settings settings;
	private Set<DriverMatch> driverMatchers;

	public DriverLoader(Settings settings, Set<String> driverDirs, Set<DriverMatch> driverMatchers) {
		this.settings = settings;
		this.loadedDrivers = new HashSet<>();
		this.driverMatchers = driverMatchers;
		URL[] urls = findUrls(driverDirs);
		this.classLoader = URLClassLoader.newInstance(urls, DriverLoader.class.getClassLoader());
	}

	private URL[] findUrls(Set<String> driverDirs) {
		Set<URL> urls = new HashSet<>();

		for (String dir : driverDirs) {
			File file = new File(dir);
			if (!file.isDirectory()) {
				continue;
			}
			File[] files = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});

			for (File jarFile : files) {
				try {
					urls.add(jarFile.toURI().toURL());
				} catch (MalformedURLException e) {
					// Ignore
				}
			}
		}
		return urls.toArray(new URL[0]);
	}

	public void loadDriver(String driverClass, String url) {
		if (driverClass == null) {
			guessClass(url);
		} else {
			ensureLoaded(driverClass);
		}
	}

	private void guessClass(String url) {
		settings.getOutput().connectMessage("Scanning for JDBC Drivers");
		for (DriverMatch match : driverMatchers) {
			if (Pattern.compile(match.getMatch()).matcher(url).find()) {
				ensureLoaded(match.getDriver());
			}
		}
	}

	private void ensureLoaded(String driver) {
		if (loadedDrivers.contains(driver)) {
			return;
		}
		boolean loaded = false;
		if (tryLoadingNormally(driver) || tryLoadingHackishly(driver)) {
			loaded = true;
		}
		loadedDrivers.add(driver);
		settings.getOutput().connectMessage((loaded ? "Loaded" : "Failed to load") + " driver " + driver);
	}

	private boolean tryLoadingHackishly(String driver) {
		try {
			Class<?> clazz = Class.forName(driver, true, classLoader);
			Object obj = clazz.newInstance();
			if (obj instanceof Driver) {
				Driver driverObj = (Driver) obj;
				DriverManager.registerDriver(new DriverDelegate(driverObj));
			}
			return true;
		} catch (Exception e) {
			// Ignore
		}
		return false;
	}

	private boolean tryLoadingNormally(String driver) {
		try {
			Class.forName(driver);
			return true;
		} catch (Exception e) {
			// Ignore
		}
		return false;
	}
}
