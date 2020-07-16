package uk.nhs.ctp.testhelper;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditUnzipper {
  public static List<ZippedEntry> unzipEntries(byte[] bytes) throws IOException {
    try (var input = new ByteArrayInputStream(bytes)) {
      try (var zip = new ZipInputStream(input)) {
        var entries = new ArrayList<ZippedEntry>();

        ZipEntry zipEntry;
        while ((zipEntry = zip.getNextEntry()) != null) {
          entries.add(ZippedEntry.builder()
              .instant(zipEntry.getCreationTime().toInstant())
              .path(zipEntry.getName())
              .body(new String(zip.readAllBytes(), UTF_8))
              .build());
        }
        zip.closeEntry();

        return entries;
      }
    }
  }

  @Value
  @Builder
  @EqualsAndHashCode
  public static class ZippedEntry {
    String path;
    String body;
    Instant instant;
  }
}
