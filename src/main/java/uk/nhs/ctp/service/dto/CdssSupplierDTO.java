package uk.nhs.ctp.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CdssSupplierDTO extends NewCdssSupplierDTO {

  private Long id;

}
