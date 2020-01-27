package uk.nhs.ctp.service.factory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.service.ReferencingContext;

@Component
@AllArgsConstructor
public class ReferencingContextFactory {

  private CdssSupplierService cdssSupplierService;

  public ReferencingContext load(Long cdssSupplierId) {
    var cdss = cdssSupplierService.getCdssSupplier(cdssSupplierId);

    return new ReferencingContext(cdss.getReferencingType());
  }
}
