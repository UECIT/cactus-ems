package uk.nhs.ctp.service;

import static org.springframework.util.StringUtils.isEmpty;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.resolver.reference.IResourceLocator;

@Service
@AllArgsConstructor
public class StorageService implements IResourceLocator {

  private IGenericClient fhirClient;

  public String storeExternal(Resource resource) {
    if (!isEmpty(resource.getId())) {
      return resource.getId();
    }

    var id = fhirClient.create().resource(resource).execute().getId();
    resource.setId(id);
    return id.getValue();
  }

  public void updateExternal(Resource resource) {
    fhirClient.update().resource(resource).execute();
  }

  public <T extends Resource> List<T> findResources(List<String> resourceReferences,
      Class<T> clazz) {
    return resourceReferences.stream()
        .map(ref -> findResource(ref, clazz))
        .collect(Collectors.toList());
  }

  public <T extends Resource> T findResource(String id, Class<T> clazz) {
    return fhirClient.read().resource(clazz).withUrl(id).execute();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IBaseResource> T findResource(IIdType idType) {
    return (T) fhirClient.read()
        .resource(idType.getResourceType())
        .withId(idType)
        .execute();
  }
}
