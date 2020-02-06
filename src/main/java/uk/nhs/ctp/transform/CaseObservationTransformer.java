package uk.nhs.ctp.transform;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CaseObservation;

@Component
@Slf4j
public class CaseObservationTransformer implements Transformer<Observation, CaseObservation> {

  @Override
  public CaseObservation transform(Observation observation) {
    CaseObservation caseObservation = new CaseObservation();

    updateObservationCoding(observation, caseObservation);

    // Try to set dataAbsenseReason here
    Coding dataAbsentReason = observation.getDataAbsentReason().getCodingFirstRep();
    caseObservation.setDataAbsentCode(dataAbsentReason.getCode());
    caseObservation.setDataAbsentDisplay(dataAbsentReason.getDisplay());
    caseObservation.setTimestamp(new Date());

    return caseObservation;
  }

  public void updateObservationCoding(Observation observation, CaseObservation caseObservation) {
    Coding coding = observation.getCode().getCodingFirstRep();
    caseObservation.setSystem(coding.getSystem());
    caseObservation.setCode(coding.getCode());
    caseObservation.setDisplay(coding.getDisplay());

    if (!observation.hasValue()) {
      caseObservation.setValueCode(null);
      caseObservation.setValueSystem(null);
      caseObservation.setValueDisplay(null);
    } else if (observation.getValue() instanceof BooleanType) {
      boolean value = observation.getValueBooleanType().booleanValue();
      caseObservation.setValueSystem("boolean");
      caseObservation.setValueCode(value ? "true" : "false");
    } else if (observation.getValue() instanceof StringType) {
      String value = observation.getValueStringType().getValue();
      caseObservation.setValueSystem("string");
      caseObservation.setValueCode(value);
    } else if (observation.getValue() instanceof CodeableConcept) {
      Coding valueCoding = observation.getValueCodeableConcept().getCodingFirstRep();
      caseObservation.setValueSystem(valueCoding.getSystem());
      caseObservation.setValueCode(valueCoding.getCode());
      caseObservation.setValueDisplay(valueCoding.getDisplay());
    } else {
      log.error("Unable assign an observation value of type {}", observation.getValue().fhirType());
    }

  }
}
