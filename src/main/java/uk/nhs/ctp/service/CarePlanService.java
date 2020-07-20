package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.utils.RetryUtils;

@Service
@RequiredArgsConstructor
public class CarePlanService {

  private final StorageService storageService;
  private final ReferenceService referenceService;

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

  public void completeCarePlans(String[] carePlanIds) {
    Stream.of(carePlanIds)
        .map(id -> storageService.findResource(id, CarePlan.class))
        .filter(carePlan -> carePlan.getStatus() == CarePlanStatus.DRAFT
          || carePlan.getStatus() == CarePlanStatus.ACTIVE)
        .map(carePlan -> carePlan.setStatus(CarePlanStatus.COMPLETED))
        .forEach(storageService::updateExternal);
  }

}
