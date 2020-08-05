package uk.nhs.ctp.transform;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.ConditionDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;

@Component
@AllArgsConstructor
public class ConditionDTOTransformer implements Transformer<Condition, ConditionDTO> {

  private final GenericResourceLocator resourceLocator;

  @Override
  public ConditionDTO transform(Condition condition) {

    List<String> evidence = condition.getEvidence().stream()
        .map(ConditionEvidenceComponent::getDetail)
        .flatMap(detail -> detail.stream()
            .map(ref -> resourceLocator.<IBaseResource>findResource(ref, condition.getIdElement()))
            .map(this::getStringValue))
        .collect(Collectors.toList());

    return ConditionDTO.builder()
        .bodySite(condition.getBodySiteFirstRep().getCodingFirstRep().getDisplay())
        .clinicalStatus(condition.getClinicalStatus().getDisplay())
        .verificationStatus(condition.getVerificationStatus().getDisplay())
        .condition(condition.getCode().getCodingFirstRep().getDisplay())
        .onset(condition.getOnsetDateTimeType().toHumanDisplay())
        .stageSummary(condition.getStage().getSummary().getCodingFirstRep().getDisplay())
        .evidence(evidence)
        .build();
  }

  private String getStringValue(IBaseResource evidence) {
    if (evidence instanceof Observation) {
      return observationString((Observation) evidence);
    } else if (evidence instanceof QuestionnaireResponse) {
      return questionnaireResponseString((QuestionnaireResponse) evidence);
    }
    throw new IllegalArgumentException("Unexpected evidence detail type " + evidence.getClass());
  }

  private String observationString(Observation observation) {
    return "Observation - " + typeString(observation.getCode())
        + " = " + typeString(observation.getValue());
  }

  private String questionnaireResponseString(QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponse.getItem().stream()
        .map(item -> "Question - " + item.getText() + " = " + item.getAnswer().stream()
            .map(answer -> typeString(answer.getValue()))
            .collect(Collectors.joining("/")))
        .collect(Collectors.joining(", "));
  }

  private String typeString(Type value) {
    if (value == null) {
      return "null";
    } else if (value instanceof Coding) {
      return codingString((Coding) value);
    } else if (value instanceof CodeableConcept) {
      return codeableConceptString((CodeableConcept) value);
    } else {
      return String.format("%s[%s]", value.getClass().getSimpleName(), value.toString());
    }
  }

  private String codeableConceptString(CodeableConcept codeableConcept) {
    if (codeableConcept.hasText()) {
      return codeableConcept.getText();
    } else if (codeableConcept.hasCoding()) {
      return codeableConcept.getCoding().stream()
          .map(this::codingString)
          .collect(Collectors.joining(", "));
    }
    return codeableConcept.toString();
  }

  private String codingString(Coding coding) {
    if (coding.hasDisplay()) {
      return coding.getDisplay();
    }

    StringBuilder sb = new StringBuilder(coding.getCode());
    if (coding.hasSystem()) {
      sb.append(" (").append(coding.getSystem()).append(")");
    }
    return sb.toString();
  }

}
