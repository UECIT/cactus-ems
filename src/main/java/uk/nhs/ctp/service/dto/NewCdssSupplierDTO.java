package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;
import uk.nhs.ctp.enums.ReferencingType;

public class NewCdssSupplierDTO {

	private String name;
	private String baseUrl;
	private List<ServiceDefinitionDTO> serviceDefinitions = new ArrayList<>();
	private ReferencingType referencingType;

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

	public ReferencingType getReferencingType() {
		return referencingType;
	}

	public void setReferencingType(ReferencingType referencingType) {
		this.referencingType = referencingType;
	}
}
