package uk.nhs.ctp.tkwvalidation;

import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;
import static uk.nhs.cactus.common.audit.model.AuditProperties.OPERATION_TYPE;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.audit.model.AuditSession;

@Component
public class InteractionAuditValidator {
  public void validate(List<AuditSession> audits) {
    var requiredProperties = List.of(OPERATION_TYPE, INTERACTION_ID);

    for (var audit : audits) {
      var properties = audit.getAdditionalProperties().keySet();
      if (!properties.containsAll(requiredProperties)) {
        throw new UnsupportedOperationException(
            "Interaction audits require the following properties set: \n"
            + String.join(", ", requiredProperties));
      }
    }
  }
}
