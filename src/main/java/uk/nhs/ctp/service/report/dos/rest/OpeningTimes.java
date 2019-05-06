package uk.nhs.ctp.service.report.dos.rest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "allHours", "days", "specifiedDates" })
public class OpeningTimes {

	@JsonProperty("allHours")
	private Boolean allHours;
	@JsonProperty("days")
	private List<Day> days = null;
	@JsonProperty("specifiedDates")
	private List<SpecifiedDate> specifiedDates = null;

	@JsonProperty("allHours")
	public Boolean getAllHours() {
		return allHours;
	}

	@JsonProperty("allHours")
	public void setAllHours(Boolean allHours) {
		this.allHours = allHours;
	}

	@JsonProperty("days")
	public List<Day> getDays() {
		return days;
	}

	@JsonProperty("days")
	public void setDays(List<Day> days) {
		this.days = days;
	}

	@JsonProperty("specifiedDates")
	public List<SpecifiedDate> getSpecifiedDates() {
		return specifiedDates;
	}

	@JsonProperty("specifiedDates")
	public void setSpecifiedDates(List<SpecifiedDate> specifiedDates) {
		this.specifiedDates = specifiedDates;
	}

}