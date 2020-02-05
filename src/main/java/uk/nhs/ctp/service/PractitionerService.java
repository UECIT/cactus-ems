package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.Practitioner;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.registry.ResourceRegistryFactory;
import uk.nhs.ctp.transform.PractitionerTransformer;

@Service
@RequiredArgsConstructor
public class PractitionerService {

  private final PractitionerTransformer practitionerTransformer;
  private Registry<Practitioner> practitionerRegistry;

  @Autowired
  private void wireRegistry(ResourceRegistryFactory registryFactory) {
    practitionerRegistry = registryFactory.getRegistry(Practitioner.class);
  }

  public List<CareConnectPractitioner> getAll() {
    return practitionerRegistry.getAll()
        .stream()
        .map(practitionerTransformer::transform)
        .collect(Collectors.toList());
  }

  public CareConnectPractitioner get(String id) {
    return practitionerRegistry.getAll()
        .stream()
        .filter(p -> p.getId().equals(id))
        .findFirst()
        .map(practitionerTransformer::transform)
        .orElseThrow();
  }
}
