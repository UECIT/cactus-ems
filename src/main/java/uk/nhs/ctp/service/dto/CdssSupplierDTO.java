package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import uk.nhs.ctp.entities.CdssSupplier;

public class CdssSupplierDTO {

  private Long id;
  private String name;
  private List<ServiceDefinitionDTO> serviceDefinitions = new ArrayList<>();

  public CdssSupplierDTO(CdssSupplier supplier) {
    this.setId(supplier.getId());
    this.setName(supplier.getName());
    this.setServiceDefinitions(
        supplier.getServiceDefinitions().stream()
            .map(ServiceDefinitionDTO::new)
            .collect(Collectors.toList())
		);
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
