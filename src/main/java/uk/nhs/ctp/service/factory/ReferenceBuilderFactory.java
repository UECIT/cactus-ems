package uk.nhs.ctp.service.factory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.service.ReferencingContext;
import uk.nhs.ctp.service.StorageService;
import uk.nhs.ctp.service.builder.ReferenceBuilder;

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
