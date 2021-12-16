package com.vidya.logprocessor;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vidya.logprocessor.service.LogService;

@SpringBootApplication
public class LogProcessorApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogProcessorApplication.class);

	@Autowired
	private LogService logService;

	public static void main(String... args) {
		SpringApplication app = new SpringApplication(LogProcessorApplication.class);
		app.run(args);
	}

	@Override
	public void run(String... args) {
		Instant startTime = Instant.now();
		this.logService.start(args);
		Instant endTime = Instant.now();
		LOGGER.info("Total time taken: {} ms", Duration.between(startTime, endTime).toMillis());
	}

}
