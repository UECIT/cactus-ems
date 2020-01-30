package uk.nhs.ctp.service;

import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Cleanly construct IDs for Resources served by the local FHIR endpoint
 */
@Service
public class LocalReferenceService {

  @Value("${ems.server}")
  private String emsServer;

  public String buildId(ResourceType resourceType, long id) {
    return UriComponentsBuilder.fromUriString(emsServer)
        .pathSegment("fhir", resourceType.name(), Long.toString(id))
        .build().toUriString();
  }

  public Reference buildRef(ResourceType resourceType, long id) {
    return new Reference(buildId(resourceType, id));
  }
}
