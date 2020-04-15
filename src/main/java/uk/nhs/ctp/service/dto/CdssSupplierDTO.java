package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.nhs.ctp.entities.CdssSupplier;

@Data
@NoArgsConstructor
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

  public void addServiceDefinition(ServiceDefinitionDTO serviceDefinition) {
    this.serviceDefinitions.add(serviceDefinition);
  }
}
