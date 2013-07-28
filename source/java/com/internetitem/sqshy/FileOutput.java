package com.internetitem.sqshy;

import java.io.FileWriter;
import java.io.IOException;

import com.internetitem.sqshy.settings.Settings;

public class FileOutput extends AbstractOutput {

	private FileWriter writer;

	public FileOutput(Settings settings, String filename, boolean append) throws IOException {
		super(settings);
		this.writer = new FileWriter(filename, append);
	}

	@Override
	protected void outputLine(String line) {
		try {
			writer.write(line);
			writer.write("\n");
			writer.flush();
		} catch (Exception e) {
			// Ignore
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (Exception e) {
			// Ignore;
		}
	}
}
