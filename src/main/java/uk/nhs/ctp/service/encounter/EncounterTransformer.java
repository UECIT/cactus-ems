package uk.nhs.ctp.service.encounter;

import static java.time.temporal.ChronoUnit.SECONDS;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.Cases;

@Service
public class EncounterTransformer {

  public Encounter transform(Cases triageCase, AuditRecord audit) {
    var encounter = new Encounter();

    encounter.setId(triageCase.getEncounterId());

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
