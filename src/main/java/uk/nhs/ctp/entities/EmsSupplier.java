package uk.nhs.ctp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ems_supplier", indexes = @Index(columnList = "supplierId"))
public class EmsSupplier extends SupplierPartitioned {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "base_url")
  private String baseUrl;

}
