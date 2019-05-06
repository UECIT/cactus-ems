package uk.nhs.ctp.service.report.dos.rest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "date", "sessions" })
public class SpecifiedDate {

	@JsonProperty("date")
	private String date;
	@JsonProperty("sessions")
	private List<Session> sessions = null;

	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	@JsonProperty("date")
	public void setDate(String date) {
		this.date = date;
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
