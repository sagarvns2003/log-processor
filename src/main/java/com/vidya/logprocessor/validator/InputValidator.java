package com.vidya.logprocessor.validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.vidya.logprocessor.model.Context;

@Component
public class InputValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(InputValidator.class);

	public void validate(Context context, String... args) {
		LOGGER.info("Validating the input arguments...");
		if (args.length < 1) {
			throw new IllegalArgumentException("Alert!!! Please provide the correct filepath to process...");
		}

		this.validateFilePath(context, args[0]);
	}

	private void validateFilePath(Context context, String logFilePath) {
		LOGGER.info("Input log file: {}", logFilePath);
		context.setLogFilePath(logFilePath);

		try {
			File file = new ClassPathResource("logs/" + logFilePath).getFile();
			if (!file.exists()) {
				file = new ClassPathResource(logFilePath).getFile();
				if (!file.exists()) {
					file = new File(logFilePath);
				}
			}

			if (!file.exists())
				throw new FileNotFoundException("Unable to open the file: " + logFilePath);

		} catch (IOException e) {
			LOGGER.error("Unable to find the specified file: {}", logFilePath);
		}
	}
}
