package uk.nhs.ctp.service.factory;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.service.ReferencingContext;
import uk.nhs.ctp.service.ReferenceStorageService;

@Component
public class ReferenceStorageServiceFactory {

  private IGenericClient fhirClient;

  public ReferenceStorageServiceFactory(IGenericClient fhirClient) {
    this.fhirClient = fhirClient;
  }

  public ReferenceStorageService load(ReferencingContext context) {
    return new ReferenceStorageService(fhirClient, context);
  }

  /**
   * @return A {@link ReferenceStorageService} assuming the {@link ReferencingType#ServerReferences}
   * referencing style
   */
  public ReferenceStorageService load() {
    return new ReferenceStorageService(fhirClient,
        new ReferencingContext(ReferencingType.ServerReferences));
  }
}
