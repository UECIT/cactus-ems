package uk.nhs.ctp.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.ErrorMessageDTO;

@Component
public class ErrorMessageTransformer implements Transformer<OperationOutcome, ErrorMessageDTO> {

  @Override
  public ErrorMessageDTO transform(OperationOutcome operationOutcome) {

    if (operationOutcome == null) {
      return null;
    }

    OperationOutcomeIssueComponent issue = operationOutcome.getIssueFirstRep();
    return ErrorMessageDTO.builder()
        .type(issue.getCode().getDisplay())
        .display(issue.getDetails().getCodingFirstRep().getDisplay())
        .diagnostic(issue.getDiagnostics())
        .build();
  }
}
