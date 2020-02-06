package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.resolver.reference.IResourceLocator;

@Service
@AllArgsConstructor
public class GenericResourceLocator implements IResourceLocator {

  private FhirContext fhirContext;

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IBaseResource> T findResource(String id) {
    IdType idType = new IdType(id);
    return (T) fhirContext.newRestfulGenericClient(idType.getBaseUrl())
        .read()
        .resource(idType.getResourceType())
        .withId(idType)
        .execute();
  }
}
