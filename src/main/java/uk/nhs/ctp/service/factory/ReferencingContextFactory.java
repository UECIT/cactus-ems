package uk.nhs.ctp.service.factory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.service.fhir.ReferencingContext;

@Component
@AllArgsConstructor
public class ReferencingContextFactory {

  private CdssSupplierService cdssSupplierService;

  public ReferencingContext load(CdssSupplier cdss) {
    return new ReferencingContext(cdss.getReferencingType());
  }
}
