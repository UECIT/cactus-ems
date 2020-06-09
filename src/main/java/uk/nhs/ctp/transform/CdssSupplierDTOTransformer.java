package uk.nhs.ctp.transform;

import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;

@Component
public class CdssSupplierDTOTransformer implements Transformer<CdssSupplier, CdssSupplierDTO> {

  @Override
  public CdssSupplierDTO transform(CdssSupplier supplier) {
    CdssSupplierDTO supplierDTO = new CdssSupplierDTO();
    supplierDTO.setId(supplier.getId());
    supplierDTO.setName(supplier.getName());
    supplierDTO.setBaseUrl(supplier.getBaseUrl());
    supplierDTO.setServiceDefinitions(
        supplier.getServiceDefinitions().stream()
            .map(ServiceDefinitionDTO::new)
            .collect(Collectors.toList())
    );
    supplierDTO.setInputDataRefType(supplier.getInputDataRefType());
    supplierDTO.setInputParamsRefType(supplier.getInputParamsRefType());
    supplierDTO.setSupportedVersion(supplier.getSupportedVersion());
    return supplierDTO;
  }
}
