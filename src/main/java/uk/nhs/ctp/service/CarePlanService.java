package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.utils.RetryUtils;

@Service
@AllArgsConstructor
@Slf4j
public class CarePlanService {

  private StorageService storageService;
  private ReferenceService referenceService;

  public List<CarePlan> getByCaseId(Long id) {
    return RetryUtils.retry(() -> storageService.getClient().search()
        .forResource(CarePlan.class)
        .where(ReferralRequest.CONTEXT.hasId(referenceService.buildId(ResourceType.Encounter, id)))
        .returnBundle(Bundle.class)
        .execute(),
        storageService.getClient().getServerBase())
        .getEntry().stream()
        .map(BundleEntryComponent::getResource)
        .map(CarePlan.class::cast)
        .collect(Collectors.toList());
  }

}
