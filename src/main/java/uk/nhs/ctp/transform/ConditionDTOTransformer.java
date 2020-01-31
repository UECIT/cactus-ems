package uk.nhs.ctp.transform;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.ReferenceService;
import uk.nhs.ctp.service.StorageService;
import uk.nhs.ctp.service.dto.ConditionDTO;

@Component
@AllArgsConstructor
public class ConditionDTOTransformer implements Transformer<Condition, ConditionDTO> {

  private StorageService storageService;
  private ReferenceService referenceService;

  @Override
  public ConditionDTO transform(Condition condition) {

    List<String> evidence = condition.getEvidence().stream()
        .map(ConditionEvidenceComponent::getDetail)
        .flatMap(detail -> detail.stream()
            .map(ref -> referenceService.resolve(ref, storageService))
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
    }
    else if (evidence instanceof QuestionnaireResponse) {
      return questionnaireResponseString((QuestionnaireResponse) evidence);
    }
    throw new IllegalArgumentException("Unexpected evidence detail type " + evidence.getClass());
  }

  private String observationString(Observation observation) {
    return "Observation - " + observation.getCode().getCodingFirstRep()
        .getDisplay() + " = " + observation.getValue();
  }

  private String questionnaireResponseString(QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponse.getItem().stream()
        .map(item -> "Question - " + item.getText() + " = " + item.getAnswer().stream()
            .map(answer -> answer.getValue().toString())
            .collect(Collectors.joining("/")))
        .collect(Collectors.joining(", "));
  }

}
