package uk.nhs.ctp.service.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.AuthenticatedFhirClientFactory;
import uk.nhs.ctp.utils.RetryUtils;

@Service
@RequiredArgsConstructor
/*
  FHIR resource storage service that is aware of the token exchange server and can
  appropriately authenticate with registered endpoints.
 */
public class AuthenticatedStorageService {

  @Value("${fhir.server}")
  private String fhirServer;

  private final AuthenticatedFhirClientFactory clientFactory;

  private IGenericClient getClientFor(String serverBase) {
    return clientFactory.getClient(serverBase);
  }
  private IGenericClient getClientFor(IdType id) {
    return getClientFor(id.getBaseUrl());
  }

  public <T extends IBaseResource> T get(String id, Class<T> type) {
    var idType = new IdType(id);
    var client = getClientFor(idType);
    var resource = RetryUtils.retry(() -> client.read()
        .resource(type)
        .withId(idType)
        .execute(),
        client.getServerBase());
    System.out.println("Obtained resource has lalala id " + resource.getIdElement());
    return resource;
  }

  /**
   * Updates a record with an existing ID, or {@link #create(Resource)} a new record if the ID is
   * missing
   *
   * @param resource the resource to update on the remote server
   * @return a reference to the stored resource
   */
  public Reference upsert(Resource resource) {
    if (resource.hasId()) {
      var client = getClientFor(resource.getIdElement().getBaseUrl());
      RetryUtils.retry(() -> client.update()
              .resource(resource)
              .execute(),
          client.getServerBase());
      return new Reference(resource.getId());
    } else {
      return create(resource);
    }
  }

  public Reference create(Resource resource) {
    if (resource.hasId()) {
      throw new IllegalArgumentException("Cannot create resource with existing id");
    }
    var client = getClientFor(fhirServer);
    var id = RetryUtils.retry(() -> client.create()
            .resource(resource).execute()
            .getId(),
        client.getServerBase());
    resource.setId(id);
    return new Reference(id);
  }
}
