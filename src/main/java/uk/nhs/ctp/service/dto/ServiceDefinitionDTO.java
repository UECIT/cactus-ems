package uk.nhs.ctp.service.dto;

import uk.nhs.ctp.entities.ServiceDefinition;

public class ServiceDefinitionDTO {

	private String serviceDefinitionId;
	private String description;

	public ServiceDefinitionDTO(ServiceDefinition sd) {
		this.setDescription(sd.getDescription());
		this.setServiceDefinitionId(sd.getServiceDefinitionId());
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public String getDescription() {
		return description;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
