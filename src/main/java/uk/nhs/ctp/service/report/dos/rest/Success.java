package uk.nhs.ctp.service.report.dos.rest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "transactionId", "servicesReturnedAreCatchAll", "serviceCount", "services" })
public class Success {

	@JsonProperty("code")
	private Integer code;
	@JsonProperty("transactionId")
	private String transactionId;
	@JsonProperty("servicesReturnedAreCatchAll")
	private String servicesReturnedAreCatchAll;
	@JsonProperty("serviceCount")
	private Integer serviceCount;
	@JsonProperty("services")
	private List<Service> services = null;

	@JsonProperty("code")
	public Integer getCode() {
		return code;
	}

	@JsonProperty("code")
	public void setCode(Integer code) {
		this.code = code;
	}

	@JsonProperty("transactionId")
	public String getTransactionId() {
		return transactionId;
	}

	@JsonProperty("transactionId")
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@JsonProperty("servicesReturnedAreCatchAll")
	public String getServicesReturnedAreCatchAll() {
		return servicesReturnedAreCatchAll;
	}

	@JsonProperty("servicesReturnedAreCatchAll")
	public void setServicesReturnedAreCatchAll(String servicesReturnedAreCatchAll) {
		this.servicesReturnedAreCatchAll = servicesReturnedAreCatchAll;
	}

	@JsonProperty("serviceCount")
	public Integer getServiceCount() {
		return serviceCount;
	}

	@JsonProperty("serviceCount")
	public void setServiceCount(Integer serviceCount) {
		this.serviceCount = serviceCount;
	}

	@JsonProperty("services")
	public List<Service> getServices() {
		return services;
	}

	@JsonProperty("services")
	public void setServices(List<Service> services) {
		this.services = services;
	}
}
