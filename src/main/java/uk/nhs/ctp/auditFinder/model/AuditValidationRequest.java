package uk.nhs.ctp.auditFinder.model;

import java.time.Instant;
import lombok.Data;

@Data
public class AuditValidationRequest {

  private String endpoint;
  private Instant auditDateTime;
  private String caseId; // For encounters

}
