package uk.nhs.ctp.tkwvalidation;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.tkwvalidation.rules.AuditValidationRule;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private final List<AuditValidationRule> validationRules;
  private final AuditSelector auditSelector;
  private final AuditZipBuilder auditZipBuilder;
  private final AuditMetadataCollector auditMetadataCollector;
  private final AuditDispatcher auditDispatcher;

  public String validateAudits(
      List<AuditSession> audits,
      OperationType operationType,
      String selectedServiceEndpoint)
      throws IOException {
    validationRules.stream()
        .filter(rule -> rule.getSupportedType() == operationType)
        .findFirst()
        .ifPresent(rule -> rule.ensure(audits));

    var messageAudits = auditSelector.selectAudits(audits, operationType);
    var zipData = auditZipBuilder.zipMessageAudits(messageAudits);
    var zipMetadata = auditMetadataCollector.collect(audits, operationType, selectedServiceEndpoint);

    return auditDispatcher.dispatchToTkw(zipData, zipMetadata);
  }

  public String getValidationUrl() {
    return auditDispatcher.getValidationUrl();
  }

}
