package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;

public class NewCdssSupplierDTO {

	private String name;
	private String baseUrl;
	private List<ServiceDefinitionDTO> serviceDefinitions = new ArrayList<>();

	public String getName() {
		return name;
	}

	public List<ServiceDefinitionDTO> getServiceDefinitions() {
		return serviceDefinitions;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setServiceDefinitions(List<ServiceDefinitionDTO> serviceDefinitions) {
		this.serviceDefinitions = serviceDefinitions;
	}

	public void addServiceDefinition(ServiceDefinitionDTO serviceDefinition) {
		this.serviceDefinitions.add(serviceDefinition);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
