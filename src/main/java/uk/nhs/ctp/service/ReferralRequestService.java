package uk.nhs.ctp.service;

import static java.util.Collections.singletonList;

import java.util.List;
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
import uk.nhs.ctp.utils.RetryUtils;

@Service
@AllArgsConstructor
@Slf4j
public class ReferralRequestService {

  private AppointmentService appointmentService;
  private StorageService storageService;
  private ReferenceService referenceService;

  public void updateServiceRequested(SelectedServiceRequestDTO requestDTO) {
    log.info("Setting selected HealthcareService for case " + requestDTO.getCaseId());
    ReferralRequest referralRequest = getByCaseId(requestDTO.getCaseId()).get(0); //TODO: CDSCT-130
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

  public List<ReferralRequest> getByCaseId(Long id) {
    return RetryUtils.retry(() -> storageService.getClient().search()
        .forResource(ReferralRequest.class)
        .where(ReferralRequest.CONTEXT.hasId(referenceService.buildId(ResourceType.Encounter, id)))
        .returnBundle(Bundle.class)
        .execute(), storageService.getClient().getServerBase())
        .getEntry().stream()
        .map(BundleEntryComponent::getResource)
        .map(ReferralRequest.class::cast)
        .collect(Collectors.toList());
  }
}
