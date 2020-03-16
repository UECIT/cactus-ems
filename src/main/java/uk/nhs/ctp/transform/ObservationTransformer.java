package uk.nhs.ctp.transform;

import static org.apache.commons.lang3.StringUtils.defaultString;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.service.NarrativeService;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Component
@RequiredArgsConstructor
public class ObservationTransformer implements Transformer<CaseObservation, Observation> {

  private final ReferenceService referenceService;
  private final NarrativeService narrativeService;

  @Override
  public Observation transform(CaseObservation caseObservation) {
    var observation = new Observation()
        .setStatus(ObservationStatus.FINAL)
        .setIssued(caseObservation.getDateCreated())
        .setCode(new CodeableConcept().addCoding(new Coding(
            caseObservation.getSystem(),
            caseObservation.getCode(),
            caseObservation.getDisplay())));

    switch (defaultString(caseObservation.getValueSystem(), "")) {
      case "boolean":
        observation.setValue(new BooleanType(caseObservation.getValueCode()));
        break;
      case "string":
        observation.setValue(new StringType(caseObservation.getValueCode()));
        break;
      default:
        observation.setValue(new CodeableConcept().addCoding(new Coding(
            caseObservation.getValueSystem(),
            caseObservation.getValueCode(),
            caseObservation.getValueDisplay())));
    }

    if (caseObservation.getDataAbsentCode() != null && caseObservation.getDataAbsentDisplay() != null) {
      observation.setDataAbsentReason(new CodeableConcept().addCoding(new Coding(
          caseObservation.getDataAbsentSystem(),
          caseObservation.getDataAbsentCode(),
          caseObservation.getDataAbsentDisplay())));
    }

    observation.setText(narrativeService.buildNarrative(transformNarrative(caseObservation)));

    Cases caseEntity = caseObservation.getCaseEntity();
    if (caseEntity != null) {
      observation.setContext(referenceService.buildRef(ResourceType.Encounter, caseEntity.getId()));
      observation.setSubject(
          referenceService.buildRef(ResourceType.Patient, caseEntity.getPatientId()));
    }

    observation.setId(referenceService.buildId(ResourceType.Observation, caseObservation.getId()));

    return observation;
  }

  private String transformNarrative(CaseObservation caseObservation) {
    var codeText = defaultString(caseObservation.getDisplay(), caseObservation.getCode());
    var text = "Observed that '" + codeText + "' was ";
    if (caseObservation.getDataAbsentCode() != null) {
      return text + "not present because '" + caseObservation.getDataAbsentDisplay() + "'";
    }

    var valueText = defaultString(caseObservation.getValueDisplay(), caseObservation.getValueCode());
    switch (defaultString(caseObservation.getValueSystem(), "")) {
      case "boolean":
      case "string":
        return text + valueText;
      default:
        return text + "'" + valueText + "'";
    }
  }
}
