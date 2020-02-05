package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.registry.HealthcareServiceRegistry;
import uk.nhs.ctp.transform.HealthcareServiceOutTransformer;

@Service
@AllArgsConstructor
public class HealthcareServiceService {

  private HealthcareServiceRegistry healthcareServiceRegistry;
  private HealthcareServiceOutTransformer healthcareServiceTransformer;

  public HealthcareService get(String id) {
    return healthcareServiceRegistry.getAll().stream()
        .filter(s -> id.equals(s.getId()))
        .findFirst()
        .map(healthcareServiceTransformer::transform)
        .orElseThrow(() -> new EMSException(HttpStatus.NOT_FOUND, id + " not found"));
  }

  public List<HealthcareService> getAll() {
    return healthcareServiceRegistry.getAll().stream()
        .map(healthcareServiceTransformer::transform)
        .collect(Collectors.toList());
  }
}
