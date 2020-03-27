package uk.nhs.ctp.transform;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.EncounterHandoverDTO;
import uk.nhs.ctp.service.dto.EncounterReportInput;

@Component
public class EncounterReportInputTransformer implements Transformer<EncounterReportInput, EncounterHandoverDTO> {

  private static final String UNKNOWN = "Unknown";

  @Override
  public EncounterHandoverDTO transform(EncounterReportInput encounterReportInput) {
    Patient patient = encounterReportInput.getPatient();
    Encounter encounter = encounterReportInput.getEncounter();

    String start, end;

    if (encounter.hasPeriod()) {
      Period period = encounter.getPeriod();
      start = period.hasStart() ? period.getStart().toString() : UNKNOWN;
      end = period.hasEnd() ? period.getEnd().toString() : UNKNOWN;
    }
    else {
      start = end = UNKNOWN;
    }

    return EncounterHandoverDTO.builder()
        .encounterId(encounter.getId())
        .encounterStart(start)
        .encounterEnd(end)
        .observations(getObservations(encounterReportInput.getObservations()))
        .patientId(patient.getId())
        .patientName(patient.getNameFirstRep().getNameAsSingleString())
        .patientAddress(getAddress(patient.getAddressFirstRep()))
        .build();
  }

  private String getAddress(Address address) {
    return new StringJoiner("\n")
        .add(address.getLine().stream().map(StringType::getValueAsString).collect(Collectors.joining(" ")))
        .add(address.getCity())
        .add(address.getPostalCode())
        .toString();
  }

  //TODO get value properly
  private List<String> getObservations(List<Observation> observations) {
    return observations.stream()
        .map(this::observationString)
        .collect(Collectors.toList());
  }

  private String observationString(Observation observation) {
    return "Observation - [" + observation.getCode().getCodingFirstRep()
        .getDisplay() + "/" +
        observation.getCode().getCodingFirstRep().getCode() + " = " +
        observation.getValue() + "]";
  }
}
