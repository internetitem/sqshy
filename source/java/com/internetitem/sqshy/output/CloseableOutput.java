package com.internetitem.sqshy.output;

import com.internetitem.sqshy.settings.Settings;

public abstract class CloseableOutput extends AbstractOutput {

	public CloseableOutput(Settings settings) {
		super(settings);
	}

	public abstract void close();
}
