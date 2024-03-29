package uk.nhs.ctp.transform;

import static java.lang.Boolean.TRUE;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.enums.ConditionCategory;
import uk.nhs.ctp.enums.ConditionCodeUEC;
import uk.nhs.ctp.enums.ParticipationType;
import uk.nhs.ctp.service.NarrativeService;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Service
@RequiredArgsConstructor
public class EncounterTransformer {

  private final ReferenceService referenceService;
  private final NarrativeService narrativeService;

  public Encounter transform(Cases caseEntity) {
    var encounter = new Encounter();

    // TODO add missing encounter fields (based on 1.0.0 guidance, as CCE guidance is sparse)
    // statusHistory
    // encounterType
    // priority
    // episodeOfCare
    // incomingReferral

    encounter.setId(caseEntity.getId().toString());
    encounter.setText(transformNarrative(caseEntity));
    encounter.setSubject(new Reference(caseEntity.getPatientId()));
    encounter.setServiceProvider(
        referenceService.buildRef(ResourceType.Organization, "self"));

    addPractitioner(caseEntity, encounter);

    encounter.setStatus(TRUE.equals(caseEntity.getTriageComplete())
        ? EncounterStatus.FINISHED
        : EncounterStatus.TRIAGED);

    setPeriod(caseEntity, encounter);

    //TODO: Contained/hard coded for now, find out which condition this should be, when it should be set and where to get it from? RefReq?
    Condition condition = new Condition();
    condition.setClinicalStatus(ConditionClinicalStatus.ACTIVE);
    condition.setVerificationStatus(ConditionVerificationStatus.CONFIRMED);
    condition.setCode(ConditionCodeUEC.CHEST_PAIN.toCodeableConcept());
    condition.setSubject(new Reference(caseEntity.getPatientId()));
    condition.setCategory(Collections.singletonList(ConditionCategory.CONCERN.toCodeableConcept()));
    encounter.addDiagnosis()
        .setCondition(new Reference(condition));

    return encounter;
  }

  private Narrative transformNarrative(Cases caseEntity) {
    String text = "A " + (TRUE.equals(caseEntity.getTriageComplete()) ? "finished" : "triaged")
        + "encounter for patient " + caseEntity.getFirstName() + " " + caseEntity.getLastName()
        + " on " + caseEntity.getCreatedDate();
    return narrativeService.buildNarrative(text);
  }

  private void addPractitioner(Cases caseEntity, Encounter encounter) {
    if (caseEntity.getPractitionerId() != null) {
      var participant = new Encounter.EncounterParticipantComponent();
      participant.addType(ParticipationType.ADM.toCodeableConcept());
      participant.addType(ParticipationType.DIS.toCodeableConcept());
      participant.setIndividual(
          referenceService.buildRef(ResourceType.Practitioner, caseEntity.getPractitionerId()));
      encounter.addParticipant(participant);
    }
  }

  private void setPeriod(Cases caseEntity, Encounter encounter) {
    var period = new Period();
    period.setStart(caseEntity.getCreatedDate());
    if (caseEntity.getClosedDate() != null) {
      period.setEnd(caseEntity.getClosedDate());

      var duration = new Duration();
      duration.setCode("s");
      duration.setSystem("http://unitsofmeasure.org");
      duration.setUnit("sec");
      duration.setValue(SECONDS.between(
          caseEntity.getCreatedDate().toInstant(),
          caseEntity.getClosedDate().toInstant()
      ));
      encounter.setLength(duration);
    }

    encounter.setPeriod(period);
  }
}
