package uk.nhs.ctp.service;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.ctp.service.resolver.reference.IResourceLocator;

/**
 * Cleanly construct IDs for Resources served by the local FHIR endpoint
 */
@Service
public class ReferenceService {

  @Value("${ems.server}")
  private String emsServer;

  public String buildId(ResourceType resourceType, long id) {
    return buildId(resourceType, Long.toString(id));
  }
  public String buildId(ResourceType resourceType, String id) {
    return UriComponentsBuilder.fromUriString(emsServer)
        .pathSegment("fhir", resourceType.name(), id)
        .build().toUriString();
  }

  public Reference buildRef(ResourceType resourceType, long id) {
    return new Reference(buildId(resourceType, id));
  }
  public Reference buildRef(ResourceType resourceType, String id) {
    return new Reference(buildId(resourceType, id));
  }

  /**
   * @deprecated use {@link #fetch(Reference, IResourceLocator, IdType)}
   */
  @Deprecated
  public IBaseResource fetch(Reference reference, IResourceLocator storageService) {
    if (reference.getResource() != null) {
      return reference.getResource();
    }
    return storageService.findResource(reference.getReference());
  }

  public IBaseResource fetch(Reference reference, IResourceLocator storageService, IdType parentId) {
    if (reference.getResource() != null) {
      return reference.getResource();
    }
    IdType idType = new IdType(reference.getReference());
    if (!idType.isAbsolute()) {
      idType = idType.withServerBase(parentId.getBaseUrl(), idType.getResourceType());
    }
    return storageService.findResource(idType.getValue());
  }
}
