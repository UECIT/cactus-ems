package uk.nhs.ctp.transform;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CaseObservation;

@Component
public class ObservationTransformer implements Transformer<CaseObservation, Observation> {

  @Override
  public Observation transform(CaseObservation caseObservation) {
    Observation observation = new Observation()
        .setStatus(ObservationStatus.FINAL)
        .setIssued(caseObservation.getTimestamp())
        .setCode(new CodeableConcept()
          .addCoding(new Coding(caseObservation.getSystem(), caseObservation.getCode(), caseObservation.getDisplay())));

    switch (StringUtils.defaultString(caseObservation.getValueSystem(), "")) {
      case "boolean":
        observation.setValue(new BooleanType(caseObservation.getValueCode()));
        break;
      case "string":
        observation.setValue(new StringType(caseObservation.getValueCode()));
        break;
      default:
        observation.setValue(
            new CodeableConcept().addCoding(new Coding(
                caseObservation.getValueSystem(),
                caseObservation.getValueCode(),
                caseObservation.getValueDisplay())
            ));
    }

    if (caseObservation.getDataAbsentCode() != null && caseObservation.getDataAbsentDisplay() != null) {
      observation
          .setDataAbsentReason(new CodeableConcept().addCoding(new Coding(
              caseObservation.getDataAbsentSystem(),
              caseObservation.getDataAbsentCode(),
              caseObservation.getDataAbsentDisplay())));
    }
    observation.setId(caseObservation.getId().toString());

    return observation;
  }
}
