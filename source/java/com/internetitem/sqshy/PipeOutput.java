package com.internetitem.sqshy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import com.internetitem.sqshy.settings.Settings;

public class PipeOutput extends AbstractOutput {

	private Process process;
	private OutputStream output;
	private OutputStreamWriter writer;

	public PipeOutput(Settings settings, List<String> arguments) throws IOException {
		super(settings);
		ProcessBuilder pb = new ProcessBuilder(arguments);
		pb.redirectErrorStream(true);
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectInput(Redirect.PIPE);
		this.process = pb.start();
		this.output = process.getOutputStream();
		this.writer = new OutputStreamWriter(output);
	}

	@Override
	protected void outputLine(String line) {
		try {
			writer.write(line);
			writer.flush();
		} catch (IOException e) {
			// Ignore
		}
	}

	public void close() {
		try {
			writer.write('\n');
			writer.close();
			output.close();
		} catch (IOException e) {
			// Ignore
		}
	}
}
