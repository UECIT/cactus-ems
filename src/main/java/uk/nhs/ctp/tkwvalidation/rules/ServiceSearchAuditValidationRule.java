package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.audit.model.AuditSession;

@Component
@Qualifier("service_search") // OperationType.SERVICE_SEARCH.getName()
public class ServiceSearchAuditValidationRule implements AuditValidationRule {

  @Override
  public void ensure(List<AuditSession> audits) {
    if (audits.size() != 1 || audits.get(0).getEntries().size() != 1) {
      throw new UnsupportedOperationException(
          "Can only zip service_search audits one at a time");
    }
  }
}
