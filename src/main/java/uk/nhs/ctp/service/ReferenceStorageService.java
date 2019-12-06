package uk.nhs.ctp.service;

import static org.springframework.util.StringUtils.isEmpty;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

@AllArgsConstructor
public class ReferenceStorageService {

  private IGenericClient fhirClient;
  private ReferencingContext context;

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

  public <T extends Resource> Reference storeExternal(T resource) {
    if (!isEmpty(resource.getId())) {
      return new Reference(resource);
    }

    var id = fhirClient.create().resource(resource).execute().getId();
    resource.setId(id);
    return new Reference(resource);
  }

  public <T extends Resource> void updateExternal(T resource) {
    fhirClient.update().resource(resource).execute();
  }

  public <T extends Resource> List<T> findResources(List<String> resourceReferences, Class<T> clazz) {
    return resourceReferences.stream()
        .map(ref -> fhirClient.read().resource(clazz).withUrl(ref).execute())
        .collect(Collectors.toList());
  }
}
