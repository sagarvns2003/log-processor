package com.vidya.logprocessor.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vidya.logprocessor.model.Event;
import com.vidya.logprocessor.model.EventType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "EVENTS")
public class Events {

	@Id
    @JsonProperty("id")
    private String id;

	@JsonProperty("duration")
	private int duration;

	@JsonProperty("type")
	private EventType type;

	@JsonProperty("host")
	private String host;

	@JsonProperty("alert")
	private Boolean alert;

	public Events() {
	}

	public Events(Event event, int duration) {
		this.id = event.getId();
		this.type = event.getType();
		this.host = event.getHost();
		this.duration = duration;
		this.alert = Boolean.FALSE;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Boolean getAlert() {
		return alert;
	}

	public void setAlert(Boolean alert) {
		this.alert = alert;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
