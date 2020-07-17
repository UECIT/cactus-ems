package uk.nhs.ctp.transform.two;

import static uk.nhs.ctp.utils.ResourceUtils.referenceTo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.ReferralRequestDTO;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.transform.ConditionDTOTransformer;
import uk.nhs.ctp.transform.ReferralRequestDTOTransformer;

@Component
@RequiredArgsConstructor
public class ReferralRequestDTOTwoTransformer implements ReferralRequestDTOTransformer {

  private final ConditionDTOTransformer conditionDTOTransformer;
  private final StorageService storageService;

  @Override
  public ReferralRequestDTO transform(ReferralRequest referralRequest) {

    Condition reasonRefCondition = storageService
        .findResource(referralRequest.getReasonReferenceFirstRep().getReference(), Condition.class);

    List<String> secondaryReasonReferences = referralRequest.getSupportingInfo().stream()
        .filter(referenceTo(ResourceType.Condition))
        .map(Reference::getReference)
        .collect(Collectors.toUnmodifiableList());
    List<Condition> secondaryReasonConditions = storageService
        .findResources(secondaryReasonReferences, Condition.class);
    Reference procedureRequestRef = referralRequest.getSupportingInfo().stream()
        .filter(referenceTo(ResourceType.ProcedureRequest))
        .findFirst().orElseThrow();
    String nextAction = storageService.findResource(procedureRequestRef.getReference(),
        ProcedureRequest.class)
        .getCode().getCodingFirstRep().getDisplay();

    return ReferralRequestDTO.builder()
        .resourceId(referralRequest.getId())
        .contextReference(referralRequest.getContext().getReference())
        .status(referralRequest.getStatus().getDisplay())
        .priority(referralRequest.getPriority().getDisplay())
        .occurrence(formatPeriod(referralRequest.getOccurrencePeriod()))
        .action("Procedure Request: " + nextAction)
        .description(referralRequest.getDescription())
        .relevantHistory(transformRelevantHistory(referralRequest.getRelevantHistory()))
        .reasonReference(conditionDTOTransformer.transform(reasonRefCondition))
        .supportingInfo(secondaryReasonConditions.stream()
            .map(conditionDTOTransformer::transform)
            .collect(Collectors.toList()))
        .build();

  }

  private String transformRelevantHistory(List<Reference> relevantHistory) {
    return relevantHistory.stream()
        .map(Reference::getDisplay)
        .collect(Collectors.joining(" + "));
  }

  private String formatPeriod(Period occurrencePeriod) {
    DateFormat formatter = new SimpleDateFormat("d MMM yyyy hh:mm:ss", Locale.UK);
    return "Start: "
        + formatter.format(occurrencePeriod.getStart())
        + " - End: "
        + formatter.format(occurrencePeriod.getEnd());
  }
}
