package uk.nhs.ctp.service.report.dos.rest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "day", "sessions" })
public class Day {

	@JsonProperty("day")
	private String day;
	@JsonProperty("sessions")
	private List<Session> sessions = null;

	@JsonProperty("day")
	public String getDay() {
		return day;
	}

	@JsonProperty("day")
	public void setDay(String day) {
		this.day = day;
	}

	@JsonProperty("sessions")
	public List<Session> getSessions() {
		return sessions;
	}

	@JsonProperty("sessions")
	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

}
