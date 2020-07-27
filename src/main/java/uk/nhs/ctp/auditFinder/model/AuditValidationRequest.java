package uk.nhs.ctp.auditFinder.model;

import lombok.Data;
import uk.nhs.cactus.common.audit.model.OperationType;

@Data
public class AuditValidationRequest {

  private OperationType type;
  private String instanceBaseUrl;
  private String interactionId;

}
