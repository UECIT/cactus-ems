package uk.nhs.ctp.tkwvalidation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.tkwvalidation.models.HttpMessageAudit;
import uk.nhs.ctp.tkwvalidation.models.HttpMessageType;
import uk.nhs.ctp.tkwvalidation.rules.AuditValidationRule;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private final Map<String, AuditValidationRule> validationRules;
  private final ZipBuilderFactory zipBuilderFactory;
  private final AuditSelector auditSelector;

  public byte[] zipAudits(
      List<AuditSession> audits,
      OperationType operationType) throws IOException {
    validationRules.get(operationType.getName()).ensure(audits);

    var zipBuilder = zipBuilderFactory.create();
    var sequenceCounter = new HashMap<String, Integer>();

    for (var messageAudit : auditSelector.selectAudits(audits, operationType)) {
        int count = sequenceCounter.compute(
            messageAudit.getFilePath(),
            (path, existingCount) -> existingCount == null ? 1 : existingCount + 1);

        addZipEntry(zipBuilder, messageAudit, count, HttpMessageType.REQUEST);
        addZipEntry(zipBuilder, messageAudit, count, HttpMessageType.RESPONSE);
    }

    return zipBuilder.buildAndCloseZip();
  }

  private static void addZipEntry(
      ZipBuilder zipBuilder,
      HttpMessageAudit audit,
      int count,
      HttpMessageType type)
      throws IOException {
    String body = null;
    switch (type) {
      case REQUEST:
        body = audit.getRequestBody();
        break;
      case RESPONSE:
        body = audit.getResponseBody();
        break;
    }

    if (body == null) {
      return;
    }

    // TODO get content type and add extension to path
    var extension = naiveIsJson(body) ? "json" : "xml";

    var fullPath = String.format(
        "%s.%d.%s.%s",
        audit.getFilePath(),
        count,
        type.name().toLowerCase(),
        extension);

    zipBuilder.addEntry(fullPath, body, audit.getMoment());
  }

  private static boolean naiveIsJson(String text) {
    return text.length() > 0 && text.charAt(0) == '{';
  }

}
