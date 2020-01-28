package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.registry.HealthcareServiceRegistry;
import uk.nhs.ctp.transform.HealthcareServiceOutTransformer;

@Service
@AllArgsConstructor
public class HealthcareServiceService {

  private HealthcareServiceRegistry healthcareServiceRegistry;
  private HealthcareServiceOutTransformer healthcareServiceTransformer;

  public List<HealthcareService> getAll() {
    return healthcareServiceRegistry.getAll().stream()
        .map(healthcareServiceTransformer::transform)
        .collect(Collectors.toList());
  }
}
