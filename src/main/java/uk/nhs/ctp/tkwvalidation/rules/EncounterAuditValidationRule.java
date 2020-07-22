package uk.nhs.ctp.tkwvalidation.rules;

import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;

@Component
public class EncounterAuditValidationRule implements AuditValidationRule {
  @Override
  public OperationType getSupportedType() {
    return OperationType.ENCOUNTER;
  }

  @Override
  public void ensure(List<AuditSession> audits) {
    if (audits.stream().anyMatch(a -> !a.getAdditionalProperties().containsKey(INTERACTION_ID))) {
      throw new UnsupportedOperationException("Encounter audits must have caseId set");
    }
  }
}
