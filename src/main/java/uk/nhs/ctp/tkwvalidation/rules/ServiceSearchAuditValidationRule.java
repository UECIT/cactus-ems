package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;

@Component
public class ServiceSearchAuditValidationRule implements AuditValidationRule {

  @Override
  public OperationType getSupportedType() {
    return OperationType.SERVICE_SEARCH;
  }

  @Override
  public void ensure(List<AuditSession> audits) {
    if (audits.size() != 1) {
      throw new UnsupportedOperationException(
          "Can only zip service_search audits one at a time");
    }
  }
}
