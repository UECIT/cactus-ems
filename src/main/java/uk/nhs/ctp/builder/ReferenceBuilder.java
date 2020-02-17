package uk.nhs.ctp.builder;

import static org.springframework.util.StringUtils.isEmpty;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import uk.nhs.ctp.service.ReferencingContext;
import uk.nhs.ctp.service.StorageService;

@AllArgsConstructor
public class ReferenceBuilder {

  private ReferencingContext context;
  private StorageService storageService;

  public Reference getReference(Resource resource) {
    if (!isEmpty(resource.getId())) {
      return new Reference(resource);
    }

    if (context.shouldUpload()) {
      var id = storageService.storeExternal(resource);
      resource.setId(id);
    }

    if (context.shouldBundle()) {
      context.getReferencedResources().add(resource);
    }

    return new Reference(resource);
  }
}
