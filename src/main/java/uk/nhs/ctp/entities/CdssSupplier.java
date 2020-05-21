package uk.nhs.ctp.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.nhs.ctp.enums.ReferencingType;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cdss_supplier", indexes = @Index(columnList = "supplierId"))
@Data
public class CdssSupplier extends SupplierPartitioned {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "base_url")
  private String baseUrl;

  @Column(name = "input_params_referencing_type")
  private ReferencingType inputParamsRefType;

  @Column(name = "input_data_referencing_type")
  private ReferencingType inputDataRefType;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "cdss_supplier_id")
  private List<ServiceDefinition> serviceDefinitions = new ArrayList<>();
}
