package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.enums.ReferencingType;

@Data
@NoArgsConstructor
public class CdssSupplierDTO {

  private Long id;
  private String name;
  private String baseUrl;
  private List<ServiceDefinitionDTO> serviceDefinitions = new ArrayList<>();
  private ReferencingType inputParamsRefType;
  private ReferencingType inputDataRefType;
  private CdsApiVersion supportedVersion;

}
