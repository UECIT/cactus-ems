package uk.nhs.ctp.service;

// Wildcard import required for Lombok UtilityClass
import static uk.nhs.ctp.utils.ResourceProviderUtils.*;

import ca.uhn.fhir.context.FhirContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.repos.ReferralRequestRepository;
import uk.nhs.ctp.service.dto.EncounterReportInput;
import uk.nhs.ctp.transform.EncounterTransformer;
import uk.nhs.ctp.transform.ObservationTransformer;
import uk.nhs.ctp.transform.ReferralRequestEntityTransformer;

@Service
@AllArgsConstructor
@Slf4j
public class EncounterService {

  private EncounterTransformer encounterTransformer;
  private ObservationTransformer observationTransformer;
  private CaseRepository caseRepository;
  private ReferralRequestRepository referralRequestRepository;
  private ReferralRequestEntityTransformer referralRequestEntityTransformer;
  private FhirContext fhirContext;

  public Encounter getEncounter(Long caseId) {
    Cases triageCase = caseRepository.findOne(caseId);
    Encounter encounter = encounterTransformer.transform(triageCase);
    encounter.setId(caseId.toString());
    return encounter;
  }

  @Transactional
  public List<Observation> getObservationsForEncounter(Long caseId) {
    List<CaseObservation> observations = caseRepository.findOne(caseId).getObservations();
    return observations.stream()
        .map(observationTransformer::transform)
        .collect(Collectors.toList());
  }

  @Transactional
  public Optional<ReferralRequest> getReferralRequestForEncounter(Long caseId) {
    ReferralRequestEntity referralRequestEntity = referralRequestRepository
        .findByCaseEntity_Id(caseId);

    if (referralRequestEntity != null) {
      return Optional.of(referralRequestEntityTransformer.transform(referralRequestEntity));
    }
    return Optional.empty();
  }

  public EncounterReportInput getEncounterReport(IdType encounterId) {
    Bundle encounterReportBundle = fhirContext.newRestfulGenericClient(encounterId.getBaseUrl())
        .operation()
        .onInstance(encounterId)
        .named("$UEC-Report")
        .withNoParameters(Parameters.class)
        .returnResourceType(Bundle.class)
        .execute();

    Encounter encounter = getResource(encounterReportBundle, Encounter.class);
    Patient patient = getResource(encounterReportBundle, Patient.class);
    List<Observation> observations = getResources(encounterReportBundle, Observation.class);
    return EncounterReportInput.builder()
        .encounter(encounter)
        .patient(patient)
        .observations(observations)
        .build();
  }

}
