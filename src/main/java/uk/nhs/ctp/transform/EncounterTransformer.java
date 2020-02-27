package uk.nhs.ctp.transform;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.enums.ConditionCategory;
import uk.nhs.ctp.enums.ParticipationType;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Service
@RequiredArgsConstructor
public class EncounterTransformer {

  private final ReferenceService referenceService;

  public Encounter transform(Cases caseEntity) {
    var encounter = new Encounter();

    encounter.setId(caseEntity.getId().toString());
    encounter.setSubject(new Reference(caseEntity.getPatientId()));
    encounter.setServiceProvider(
        referenceService.buildRef(ResourceType.Organization, "self"));

    if (caseEntity.getPractitionerId() != null) {
      var participant = new Encounter.EncounterParticipantComponent();
      participant.addType(ParticipationType.PPRF.toCodeableConcept());
      participant.addType(ParticipationType.ADM.toCodeableConcept());
      participant.addType(ParticipationType.DIS.toCodeableConcept());
      participant.setIndividual(
          referenceService.buildRef(ResourceType.Practitioner, caseEntity.getPractitionerId()));
      encounter.addParticipant(participant);
    }

    encounter.setStatus(caseEntity.isTriageComplete()
        ? EncounterStatus.FINISHED
        : EncounterStatus.TRIAGED);
    encounter.setClass_(
        new Coding("encounterCode", "unscheduled", "Unscheduled"));

    var period = new Period();
    period.setStart(caseEntity.getCreatedDate());
    if (caseEntity.getClosedDate() != null) {
      period.setEnd(caseEntity.getClosedDate());

      var duration = new Duration();
      duration.setUnit("seconds");
      duration.setValue(SECONDS.between(
          caseEntity.getCreatedDate().toInstant(),
          caseEntity.getClosedDate().toInstant()
      ));
      encounter.setLength(duration);
    }

    encounter.setPeriod(period);

    //TODO: Contained/hard coded for now, find out which condition this should be, when it should be set and where to get it from? RefReq?
    Condition condition = new Condition();
    condition.setVerificationStatus(ConditionVerificationStatus.CONFIRMED);
    condition.setCode(
        new CodeableConcept().addCoding(new Coding("ems", "47658378", "Diagnosis Condition")));
    condition.setSubject(new Reference(caseEntity.getPatientId()));
    condition.setCategory(
        Collections.singletonList(ConditionCategory.ENCOUNTER_DIAGNOSIS.toCodeableConcept()));
    encounter.addDiagnosis()
        .setCondition(new Reference(condition));

    return encounter;
  }
}
