package uk.nhs.ctp.service;

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
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.CaseCarePlan;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.EncounterEntity;
import uk.nhs.ctp.entities.IdVersion;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.repos.AuditRecordRepository;
import uk.nhs.ctp.repos.EncounterRepository;
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
  private EncounterRepository encounterRepository;
  private AuditRecordRepository auditRecordRepository;
  private ReferralRequestRepository referralRequestRepository;
  private ReferralRequestEntityTransformer referralRequestEntityTransformer;
  private FhirContext fhirContext;

  public Encounter getEncounter(Long caseId, Long version) {
    EncounterEntity triageCase = version == null
        ? encounterRepository.findFirstByIdVersion_IdOrderByIdVersion_VersionDesc(caseId)
        : encounterRepository.findOne(new IdVersion(caseId, version));
    AuditRecord auditRecord = auditRecordRepository.findByEncounterId(caseId);
    Encounter encounter = encounterTransformer.transform(triageCase, auditRecord);
    encounter.setId(caseId.toString());
    return encounter;
  }

  @Transactional
  public List<Observation> getObservationsForEncounter(Long caseId) {
    List<CaseObservation> observations = encounterRepository.findFirstByIdVersion_IdOrderByIdVersion_VersionDesc(caseId).getObservations();
    return observations.stream()
        .map(observationTransformer::transform)
        .collect(Collectors.toList());
  }

  @Transactional
  public List<CaseCarePlan> getCarePlansForEncounter(Long caseId) {
    return encounterRepository.findFirstByIdVersion_IdOrderByIdVersion_VersionDesc(caseId).getCarePlans();
  }

  @Transactional
  public Optional<ReferralRequest> getReferralRequestForEncounter(Long caseId) {
    ReferralRequestEntity referralRequestEntity = referralRequestRepository
        .findByEncounterEntity_Id(caseId);

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
