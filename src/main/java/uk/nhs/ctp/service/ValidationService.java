package uk.nhs.ctp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.auditFinder.finder.AuditFinder;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private final AuditFinder auditFinder;

  public byte[] zipResources(Long caseId) throws IOException {
    var audits = auditFinder.findAll(caseId);
    var auditEntries = audits.stream()
        .flatMap(a -> a.getEntries().stream())
        .collect(Collectors.toUnmodifiableList());

    var output = new ByteArrayOutputStream();
    var zip = new ZipOutputStream(output);

    var bundledResources = new HashSet<String>();

    for (AuditEntry entry : auditEntries) {
      if (!entry.getRequestMethod().equals("GET")) {
        continue;
      }

      URI uri = URI.create(entry.getRequestUrl());
      String path = "encounter" + caseId + "/" + uri.getHost() + uri.getPath();

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

    zip.closeEntry();
    zip.close();

    return output.toByteArray();
  }
}
