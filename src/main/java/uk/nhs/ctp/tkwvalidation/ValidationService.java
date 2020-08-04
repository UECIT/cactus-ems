package uk.nhs.ctp.tkwvalidation;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private final InteractionAuditValidator validationRule;
  private final AuditSelector auditSelector;
  private final AuditZipBuilder auditZipBuilder;
  private final AuditMetadataCollector auditMetadataCollector;
  private final AuditDispatcher auditDispatcher;

  public void validateAudits(
      List<AuditSession> audits,
      OperationType operationType,
      String selectedServiceEndpoint)
      throws IOException {

    byte[] zipData = getZipData(audits, operationType);
    AuditMetadata zipMetadata = getAuditMetadata(audits, operationType, selectedServiceEndpoint);

    auditDispatcher.dispatchToTkw(zipData, zipMetadata);
  }

  public AuditMetadata getAuditMetadata(List<AuditSession> audits, OperationType operationType,
      String selectedServiceEndpoint) {
    return auditMetadataCollector.collect(audits, operationType, selectedServiceEndpoint);
  }

  public byte[] getZipData(List<AuditSession> audits, OperationType operationType)
      throws IOException {
    validationRule.validate(audits);

    var messageAudits = auditSelector.selectAudits(audits, operationType);
    return auditZipBuilder.zipMessageAudits(messageAudits);
  }

  public String getValidationUrl() {
    return auditDispatcher.getValidationUrl();
  }

}
