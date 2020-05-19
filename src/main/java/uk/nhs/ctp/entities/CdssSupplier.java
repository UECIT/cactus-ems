package uk.nhs.ctp.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import uk.nhs.ctp.enums.ReferencingType;

@Entity
@Table(name = "cdss_supplier")
@Data
public class CdssSupplier extends SupplierPartitioned {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "base_url")
  private String baseUrl;

  @Column(name = "referencing_type")
  private ReferencingType referencingType;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "cdss_supplier_id")
  private List<ServiceDefinition> serviceDefinitions;
}
