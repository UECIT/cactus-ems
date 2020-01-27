package uk.nhs.ctp.entities;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "service_definition")
@Data
public class ServiceDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "cdss_supplier_id", nullable = true)
  private Long cdssSupplierId;

  @Column(name = "service_definition_id")
  private String serviceDefinitionId;

  @Column(name = "description")
  private String description;
}
