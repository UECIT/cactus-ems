package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;

public interface AuditValidationRule {
  OperationType getSupportedType();
  void ensure(List<AuditSession> audits);
}
