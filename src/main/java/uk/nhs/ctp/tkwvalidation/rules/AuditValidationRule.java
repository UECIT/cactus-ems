package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;

public interface AuditValidationRule {
  OperationType getSupportedType();
  void ensure(List<AuditSession> audits);
}
