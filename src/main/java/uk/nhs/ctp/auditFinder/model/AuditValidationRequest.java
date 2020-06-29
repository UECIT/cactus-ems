package uk.nhs.ctp.auditFinder.model;

import lombok.Data;

@Data
public class AuditValidationRequest {

  private Long supplierInstanceId;
  private String searchAuditId; // For service searches
  private String caseId; // For encounters

}
