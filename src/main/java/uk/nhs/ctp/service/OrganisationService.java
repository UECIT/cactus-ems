package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.Organisation;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.registry.ResourceRegistryFactory;
import uk.nhs.ctp.transform.OrganisationTransformer;

@Service
@RequiredArgsConstructor
public class OrganisationService {

  private final OrganisationTransformer organisationTransformer;
  private Registry<Organisation> organisationRegistry;

  @Autowired
  private void wireRegistry(ResourceRegistryFactory registryFactory) {
    organisationRegistry = registryFactory.getRegistry(Organisation.class);
  }

  public List<CareConnectOrganization> getAll() {
    return organisationRegistry.getAll()
        .stream()
        .map(organisationTransformer::transform)
        .collect(Collectors.toList());
  }

  public CareConnectOrganization get(String id) {
    return organisationRegistry.getAll()
        .stream()
        .filter(p -> p.getId().equals(id))
        .findFirst()
        .map(organisationTransformer::transform)
        .orElseThrow();
  }
}
