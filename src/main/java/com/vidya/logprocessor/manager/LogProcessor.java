package com.vidya.logprocessor.manager;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vidya.logprocessor.config.DataConfig;
import com.vidya.logprocessor.model.Context;
import com.vidya.logprocessor.model.Event;
import com.vidya.logprocessor.model.State;
import com.vidya.logprocessor.repository.EventsRepository;
import com.vidya.logprocessor.repository.entity.Events;

@Component
public class LogProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogProcessor.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DataConfig dataConfig;

	@Autowired
	private EventsRepository eventsRepository;

	public void process(Context context) {

		// Holds matching STARTED or FINISHED events
		Map<String, Event> eventMap = new HashMap<>();

		// Holds events that are parsed
		Map<String, Events> alerts = new HashMap<>();

		LOGGER.info("Processing events... (Bigger file may take some time)");
		try (LineIterator li = FileUtils
				.lineIterator(new ClassPathResource("logs/" + context.getLogFilePath()).getFile())) {

			while (li.hasNext()) {
				Event event;
				try {
					event = this.objectMapper.readValue(li.nextLine(), Event.class);
					LOGGER.trace("{}", event);

					// Check if, either STARTED or FINISHED event already for the given ID.
					// If yes, then find the execution time between STARTED and FINISHED states and
					// update the alert.
					if (eventMap.containsKey(event.getId())) {
						Event e1 = eventMap.get(event.getId());
						long executionTime = getEventExecutionTime(event, e1);
						Events alert = new Events(event, Math.toIntExact(executionTime));

						// Flag the alert as TRUE if the execution time is more than the specified
						// threshold
						if (executionTime > this.dataConfig.getAlertThresholdMs()) {
							alert.setAlert(Boolean.TRUE);
							LOGGER.info("Execution time for the event:{} is {}ms", event.getId(), executionTime);
						}

						alerts.put(event.getId(), alert);

						// We already found the matching event, so remove from the map
						eventMap.remove(event.getId());
					} else {
						eventMap.put(event.getId(), event);
					}

				} catch (JsonProcessingException e) {
					LOGGER.error("Unable to parse the event... ", e);
				}

				if (alerts.size() > dataConfig.getTableRowsWriteoffCount()) {
					saveEvents(alerts.values());
					alerts = new HashMap<>();
				}

			}

			if (alerts.size() != 0) {
				saveEvents(alerts.values());
			}

		} catch (IOException e) {
			LOGGER.error("!!! Unable to access the file: {}", e.getMessage());
		}
	}

	private void saveEvents(Collection<Events> alerts) {
		LOGGER.debug("Saving total:{} events...", alerts.size());
		eventsRepository.saveAll(alerts);
	}

	private long getEventExecutionTime(Event event1, Event event2) {
		Event endEvent = Stream.of(event1, event2).filter(e -> State.FINISHED.equals(e.getState())).findFirst()
				.orElse(null);
		Event startEvent = Stream.of(event1, event2).filter(e -> State.STARTED.equals(e.getState())).findFirst()
				.orElse(null);

		return Objects.requireNonNull(endEvent).getTimestamp() - Objects.requireNonNull(startEvent).getTimestamp();
	}
}
