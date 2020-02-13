package uk.nhs.ctp.transform;

import static java.time.temporal.ChronoUnit.SECONDS;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.EncounterEntity;
import uk.nhs.ctp.enums.ParticipationType;
import uk.nhs.ctp.service.ReferenceService;

@Service
@RequiredArgsConstructor
public class EncounterTransformer {

  private final ReferenceService referenceService;

  public org.hl7.fhir.dstu3.model.Encounter transform(EncounterEntity caseEntity, AuditRecord audit) {
    var encounter = new org.hl7.fhir.dstu3.model.Encounter();
    String id = caseEntity.getIdVersion().getId().toString();
    String version = caseEntity.getIdVersion().getVersion().toString();
    encounter.setId(new IdType(id).withVersion(version));
    encounter.setSubject(new Reference(caseEntity.getPatientId()));

    var participant = new org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent();
    participant.addType(ParticipationType.PPRF.toCodeableConcept());
    participant.addType(ParticipationType.ADM.toCodeableConcept());
    participant.addType(ParticipationType.DIS.toCodeableConcept());
    participant.setIndividual(
        referenceService.buildRef(ResourceType.Practitioner, caseEntity.getPractitionerId()));
    encounter.addParticipant(participant);

    // Add fields provided by audit
    if (audit != null) {
      encounter.setStatus(audit.isTriageComplete()
          ? EncounterStatus.FINISHED
          : EncounterStatus.TRIAGED);
      encounter.setClass_(
          new Coding("encounterCode", "unscheduled", "Unscheduled"));

      var period = new Period();
      period.setStart(audit.getCreatedDate());
      if (audit.getClosedDate() != null) {
        period.setEnd(audit.getClosedDate());

        var duration = new Duration();
        duration.setUnit("seconds");
        duration.setValue(SECONDS.between(
            audit.getCreatedDate().toInstant(),
            audit.getClosedDate().toInstant()
        ));
        encounter.setLength(duration);
      }

      encounter.setPeriod(period);
    }

    return encounter;
  }
}
