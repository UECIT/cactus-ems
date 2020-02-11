package uk.nhs.ctp.service;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.IdType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.Practitioner;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.transform.PractitionerTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@RequiredArgsConstructor
public class PractitionerService {

  private final PractitionerTransformer practitionerTransformer;
  private final Registry<Practitioner> practitionerRegistry;

  public List<CareConnectPractitioner> getAll() {
    return practitionerRegistry.getAll()
        .stream()
        .map(practitionerTransformer::transform)
        .collect(Collectors.toList());
  }

  public CareConnectPractitioner get(IdType id) {
    return practitionerRegistry.getAll()
        .stream()
        .filter(p -> p.getId().equals(id.getIdPart()))
        .findFirst()
        .map(practitionerTransformer::transform)
        .orElseThrow(() -> new ResourceNotFoundException(id));
  }
}
