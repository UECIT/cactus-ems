package uk.nhs.ctp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private static final String CASE_ID = "caseId";

  public byte[] zipAudits(List<AuditSession> audits, OperationType operationType) throws IOException {
    switch (operationType) {
      case ENCOUNTER:
        if (audits.stream().anyMatch(a -> !a.getAdditionalProperties().containsKey(CASE_ID))) {
          throw new UnsupportedOperationException("Encounter audits must have caseId set");
        }
        break;
      case SERVICE_SEARCH:
        if (audits.size() != 1 || audits.get(0).getEntries().size() != 1) {
          throw new UnsupportedOperationException(
              "Can only zip service_search audits one at a time");
        }
        break;
      default:
        throw new UnsupportedOperationException("Non-standard audit operation types not supported");
    }

    var output = new ByteArrayOutputStream();
    var zip = new ZipOutputStream(output);

    var bundledResources = new HashSet<String>();

    for (var audit : audits) {

      var baseName = operationType == OperationType.SERVICE_SEARCH
          ? operationType.getName()
          : "encounter" + audit.getAdditionalProperties().get(CASE_ID);

      for (var entry : audit.getEntries()) {
        if (!entry.getRequestMethod().equals("GET")) {
          continue;
        }

        URI uri = URI.create(entry.getRequestUrl());
        String path = baseName + "/" + uri.getHost() + uri.getPath();

        // TODO get content type and add extension to path
        if (entry.getResponseBody().length() > 0 && entry.getResponseBody().charAt(0) == '{') {
          path += ".json";
        } else {
          path += ".xml";
        }

        if (!bundledResources.add(path)) {
          continue;
        }

        ZipEntry zipEntry = new ZipEntry(path);
        zipEntry.setCreationTime(FileTime.from(entry.getDateOfEntry()));
        zip.putNextEntry(zipEntry);
        zip.write(entry.getResponseBody().getBytes(StandardCharsets.UTF_8));
      }
    }

    zip.closeEntry();
    zip.close();

    return output.toByteArray();
  }
}
