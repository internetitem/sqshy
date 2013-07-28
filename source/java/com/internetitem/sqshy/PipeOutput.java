package com.internetitem.sqshy;

import java.io.IOException;
import java.util.List;

import com.internetitem.sqshy.settings.Settings;

public class PipeOutput extends AbstractOutput {

	public PipeOutput(Settings settings, String executable, List<String> arguments) throws IOException {
		super(settings);
		// TODO Open process
	}

	@Override
	protected void outputLine(String line) {
		// TODO Implement
	}

	public void close() {
		// TODO Close process

	}
}
