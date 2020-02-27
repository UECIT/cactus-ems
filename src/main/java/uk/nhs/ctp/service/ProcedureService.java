package uk.nhs.ctp.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.dstu3.model.Procedure.ProcedureStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProcedureService {

  private EncounterService encounterService;
  private ReferenceService referenceService;

  public Collection<Procedure> getByCaseId(Long caseId) {

    CodeableConcept code = new CodeableConcept()
        .addCoding(new Coding("ems", "123456", "Code"));
    CodeableConcept reason = new CodeableConcept()
        .addCoding(new Coding("ems", "654311", "Reason"));
    Encounter encounter = encounterService.getEncounter(caseId);

    Procedure procedure = new Procedure()
        .setCode(code)
        .addReasonCode(reason)
        .setStatus(ProcedureStatus.PREPARATION)
        .setPerformed(new DateTimeType(Calendar.getInstance()))
        .setContext(referenceService.buildRef(ResourceType.Encounter, caseId))
        .setSubject(encounter.getSubject());

    procedure.setId(new IdType(1L));
    return Collections.singletonList(procedure);
  }

}
