package uk.nhs.ctp.service.report.dos.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "start", "end" })
public class Session {

	@JsonProperty("start")
	private Time start;
	@JsonProperty("end")
	private Time end;

	@JsonProperty("start")
	public Time getStart() {
		return start;
	}

	@JsonProperty("start")
	public void setStart(Time start) {
		this.start = start;
	}

	@JsonProperty("end")
	public Time getEnd() {
		return end;
	}

	@JsonProperty("end")
	public void setEnd(Time end) {
		this.end = end;
	}

	/**
	 * Time
	 * 
	 * @author wilcockl
	 *
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "hours", "minutes" })
	public class Time {

		@JsonProperty("hours")
		private String hours;
		@JsonProperty("minutes")
		private String minutes;

		@JsonProperty("hours")
		public String getHours() {
			return hours;
		}

		@JsonProperty("hours")
		public void setHours(String hours) {
			this.hours = hours;
		}

		@JsonProperty("minutes")
		public String getMinutes() {
			return minutes;
		}

		@JsonProperty("minutes")
		public void setMinutes(String minutes) {
			this.minutes = minutes;
		}
	}

}
