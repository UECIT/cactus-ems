package uk.nhs.ctp.service.resolver.reference;

import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

@Component
public class ReferenceResolver {

  public IBaseResource resolve(Reference reference, IResourceLocator storageService) {
    if (reference.getResource() != null) {
      return reference.getResource();
    }
    return storageService.findResource(reference.getReference());
  }

}
