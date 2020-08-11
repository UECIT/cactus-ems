package uk.nhs.ctp.tkwvalidation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;
import uk.nhs.ctp.tkwvalidation.model.HttpMessageType;

@Component
@RequiredArgsConstructor
public class AuditZipBuilder {
  private final ZipBuilderFactory zipBuilderFactory;

  public byte[] zipMessageAudits(List<FhirMessageAudit> audits) throws IOException {
    var zipBuilder = zipBuilderFactory.create();
    var sequenceCounter = new HashMap<String, Integer>();

    for (var messageAudit : audits) {
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
      FhirMessageAudit audit,
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

    zipBuilder.addEntry(fullPath, audit.getFullUrl(), body, audit.getMoment());
  }

  private static boolean naiveIsJson(String text) {
    return text.length() > 0 && text.charAt(0) == '{';
  }
}
