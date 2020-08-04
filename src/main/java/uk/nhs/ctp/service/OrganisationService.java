package uk.nhs.ctp.service;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.IdType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.Organisation;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.transform.OrganisationTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@RequiredArgsConstructor
public class OrganisationService {

  private final OrganisationTransformer organisationTransformer;
  private final Registry<Organisation> organisationRegistry;

  public List<CareConnectOrganization> getAll() {
    return organisationRegistry.getAll()
        .stream()
        .map(organisationTransformer::transform)
        .collect(Collectors.toList());
  }

  public CareConnectOrganization get(IdType id) {
    return organisationRegistry.getAll()
        .stream()
        .filter(p -> p.getId().equals(id.getIdPart()))
        .findFirst()
        .map(organisationTransformer::transform)
        .orElseThrow(() -> new ResourceNotFoundException(id));
  }
}
