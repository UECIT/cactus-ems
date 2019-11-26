package uk.nhs.ctp.service;

import static org.springframework.util.StringUtils.isEmpty;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

public class ReferenceStorageService {

  private IGenericClient fhirClient;
  private ReferencingContext context;

  public ReferenceStorageService(IGenericClient fhirClient,
      ReferencingContext context) {
    this.fhirClient = fhirClient;
    this.context = context;
  }

  public Reference store(Resource resource) {
    if (!isEmpty(resource.getId())) {
      return new Reference(resource);
    }

    if (context.shouldUpload()) {
      var id = fhirClient.create().resource(resource).execute().getId();
      resource.setId(id);
    }

    if (context.shouldBundle()) {
      context.getReferencedResources().add(resource);
    }

    return new Reference(resource);
  }
}
