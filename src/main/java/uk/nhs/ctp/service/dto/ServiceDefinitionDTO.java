package uk.nhs.ctp.service.dto;

import uk.nhs.ctp.entities.ServiceDefinition;

public class ServiceDefinitionDTO {

	private String serviceDefinitionId;
	private String description;

	public ServiceDefinitionDTO() {	}

	public ServiceDefinitionDTO(ServiceDefinition serviceDefinition) {
		this.description = serviceDefinition.getDescription();
		this.serviceDefinitionId = serviceDefinition.getServiceDefinitionId();
	}

	public ServiceDefinitionDTO(org.hl7.fhir.dstu3.model.ServiceDefinition serviceDefinition) {
		this.description = serviceDefinition.getDescription();
		this.serviceDefinitionId = serviceDefinition.getIdElement().getIdPart();
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
