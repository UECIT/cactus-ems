package uk.nhs.ctp.service.factory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.service.fhir.ReferencingContext;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.builder.ReferenceBuilder;

@Component
@AllArgsConstructor
public class ReferenceBuilderFactory {

  private StorageService storageService;

  public ReferenceBuilder load(ReferencingContext context) {
    return new ReferenceBuilder(context, storageService);
  }

  /**
   * @return A {@link ReferenceBuilder} assuming the {@link ReferencingType#ServerReferences}
   * referencing style
   */
  public ReferenceBuilder load() {
    return new ReferenceBuilder(
        new ReferencingContext(ReferencingType.ServerReferences),
        storageService);
  }
}
