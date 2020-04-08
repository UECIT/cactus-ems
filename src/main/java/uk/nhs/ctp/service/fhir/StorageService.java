package uk.nhs.ctp.service.fhir;

import static org.springframework.util.StringUtils.isEmpty;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageService implements IResourceLocator {

  @Value("${fhir.server}")
  private String fhirServer;

  private final FhirContext fhirContext;

  public String storeExternal(Resource resource) {
    if (!isEmpty(resource.getId())) {
      return resource.getId();
    }

    var id = getClient().create()
        .resource(resource)
        .execute()
        .getId();
    resource.setId(id);
    return id.getValue();
  }

  public IGenericClient getClient() {
    return fhirContext.newRestfulGenericClient(fhirServer);
  }

  public void updateExternal(Resource resource) {
    getClient().update()
        .resource(resource)
        .execute();
  }

  public <T extends Resource> List<T> findResources(List<String> resourceReferences,
      Class<T> clazz) {
    return resourceReferences.stream()
        .map(ref -> findResource(ref, clazz))
        .collect(Collectors.toList());
  }

  public <T extends Resource> T findResource(String id, Class<T> clazz) {
    return getClient().read()
        .resource(clazz)
        .withUrl(id)
        .execute();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IBaseResource> T findResource(IIdType idType) {
    return (T) getClient().read()
        .resource(idType.getResourceType())
        .withUrl(idType)
        .execute();
  }
}
