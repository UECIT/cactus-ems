package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;

@Component
public class EncounterAuditValidationRule implements AuditValidationRule {

  private static final String CASE_ID = "caseId";

  @Override
  public OperationType getSupportedType() {
    return OperationType.ENCOUNTER;
  }

  @Override
  public void ensure(List<AuditSession> audits) {
    if (audits.stream().anyMatch(a -> !a.getAdditionalProperties().containsKey(CASE_ID))) {
      throw new UnsupportedOperationException("Encounter audits must have caseId set");
    }
  }
}
