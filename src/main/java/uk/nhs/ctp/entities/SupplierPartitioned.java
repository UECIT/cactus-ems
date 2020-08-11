package uk.nhs.ctp.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class SupplierPartitioned {

  @Column(name = "supplierId", length = 45)
  private String supplierId;

}
