package com.vidya.logprocessor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vidya.logprocessor.manager.LogProcessor;
import com.vidya.logprocessor.model.Context;
import com.vidya.logprocessor.validator.InputValidator;

@Service
public class LogService {

	@Autowired
	private InputValidator inputValidator;

	@Autowired
	private LogProcessor logProcessor;

	public void start(String... args) {
		Context context = Context.getInstance();
		this.inputValidator.validate(context, args);
		this.logProcessor.process(context);
	}

}