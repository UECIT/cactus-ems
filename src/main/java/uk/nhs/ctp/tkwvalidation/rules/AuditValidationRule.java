package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import uk.nhs.ctp.audit.model.AuditSession;

public interface AuditValidationRule {
  void ensure(List<AuditSession> audits);
}
