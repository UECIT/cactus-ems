package uk.nhs.ctp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.Audit;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.repos.AuditRepository;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private final AuditRepository auditRepository;

  public byte[] zipResources(Long caseId) throws IOException {
    List<Audit> audits = auditRepository.findAllByCaseId(caseId);
    AuditEntry[] auditEntries = audits.stream()
        .flatMap(a -> a.getAuditEntries().stream())
        .toArray(AuditEntry[]::new);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ZipOutputStream zip = new ZipOutputStream(output);

    Set<String> bundledResources = new HashSet<>();

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
      zipEntry.setCreationTime(FileTime.from(entry.getCreatedDate().toInstant()));
      zip.putNextEntry(zipEntry);
      zip.write(entry.getResponseBody().getBytes(StandardCharsets.UTF_8));
    }

    zip.closeEntry();
    zip.close();

    return output.toByteArray();
  }
}
