package uk.nhs.ctp.service;

import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.dto.SelectedServiceRequestDTO;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;

@Service
@AllArgsConstructor
@Slf4j
public class ReferralRequestService {

  private AppointmentService appointmentService;
  private StorageService storageService;
  private ReferenceService referenceService;

  public void updateServiceRequested(SelectedServiceRequestDTO requestDTO) {
    log.info("Setting selected HealthcareService for case " + requestDTO.getCaseId());
    ReferralRequest referralRequest = getByCaseId(requestDTO.getCaseId()).orElseThrow();
    List<CodeableConcept> serviceRequested = requestDTO.getServiceTypes().stream()
        .map(codeDTO ->
            new CodeableConcept().addCoding(
                new Coding(codeDTO.getSystem(), codeDTO.getCode(), codeDTO.getDisplay())))
        .collect(Collectors.toList());
    referralRequest.setServiceRequested(serviceRequested);
    referralRequest.setRecipient(singletonList(new Reference(requestDTO.getSelectedServiceId())));
    appointmentService.create(referralRequest); //Create a static appointment for the referral request.
    storageService.updateExternal(referralRequest);
  }

  public Optional<ReferralRequest> getByCaseId(Long id) {
    return storageService.getClient().search()
        .forResource(ReferralRequest.class)
        .where(ReferralRequest.CONTEXT.hasId(referenceService.buildId(ResourceType.Encounter, id)))
        .returnBundle(Bundle.class)
        .execute()
        .getEntry().stream()
        .map(BundleEntryComponent::getResource)
        .map(ReferralRequest.class::cast)
        .findFirst();
  }
}
