package uk.nhs.ctp.auditFinder.model;

import java.time.Instant;
import lombok.Value;
import uk.nhs.cactus.common.audit.model.OperationType;

@Value
public class AuditInteraction {
  OperationType type;
  String interactionId;
  Instant startedAt;
}
