package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;

@Value
@Builder
public class EncounterReportInput {

  Encounter encounter;
  Patient patient;
  List<Observation> observations;

}
