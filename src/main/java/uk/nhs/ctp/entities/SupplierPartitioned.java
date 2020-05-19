package uk.nhs.ctp.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class SupplierPartitioned {

  @Column(name = "supplierId")
  private String supplierId;

}
