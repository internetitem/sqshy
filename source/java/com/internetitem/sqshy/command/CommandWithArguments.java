package com.internetitem.sqshy.command;

import java.util.ArrayList;
import java.util.List;

import com.internetitem.sqshy.output.Output;
import com.internetitem.sqshy.settings.Settings;
import com.internetitem.sqshy.util.StringConsumer;

public abstract class CommandWithArguments implements Command {

	private Settings settings;
	private List<String> parameters;

	public CommandWithArguments(Settings settings) {
		this.settings = settings;
		this.parameters = new ArrayList<>();
	}

	@Override
	public String getPrompt() {
		return null;
	}

	@Override
	public boolean isMultiline() {
		return false;
	}

	@Override
	public boolean couldBeDone() {
		return true;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public void newline() {
		// No-op
	}

	protected abstract boolean interpolateNext(List<String> parameters);

	protected abstract void execute(Output output, Settings settings, List<String> parameters) throws CommandException;

	@Override
	public void execute(Output output) throws CommandException {
		execute(output, settings, parameters);
	}

	@Override
	public void consume(boolean first, StringConsumer consumer) throws CommandException {
		parameters.add(consumer.consumeArg(interpolateNext(parameters), settings));
	}
}
