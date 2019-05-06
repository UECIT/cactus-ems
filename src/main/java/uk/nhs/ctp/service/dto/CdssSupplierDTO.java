package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;

import uk.nhs.ctp.entities.CdssSupplier;

public class CdssSupplierDTO {

	private Long id;
	private String name;
	private List<ServiceDefinitionDTO> serviceDefinitions = new ArrayList<>();

	public CdssSupplierDTO(CdssSupplier supplier) {
		this.setId(supplier.getId());
		this.setName(supplier.getName());
		supplier.getServiceDefinitions().forEach(sd -> {
			ServiceDefinitionDTO sdDTO = new ServiceDefinitionDTO(sd);
			this.addServiceDefinition(sdDTO);
		});
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ServiceDefinitionDTO> getServiceDefinitions() {
		return serviceDefinitions;
	}

	public void setId(Long id) {
		this.id = id;
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
}
