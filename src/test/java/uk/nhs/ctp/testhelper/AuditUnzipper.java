package uk.nhs.ctp.testhelper;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
          byte[] extra = zipEntry.getExtra();
          entries.add(ZippedEntry.builder()
              .instant(zipEntry.getCreationTime().toInstant())
              .path(zipEntry.getName())
              .fullUrl(getStringField((short) 0x0707, zipEntry.getExtra()))
              .body(new String(zip.readAllBytes(), UTF_8))
              .build());
        }
        zip.closeEntry();

        return entries;
      }
    }
  }

  private static String getStringField(short code, byte[] extra) {
    var buffer = ByteBuffer.wrap(extra);
    while (buffer.remaining() > 4) {
      short fieldCode = Short.reverseBytes(buffer.getShort());
      short fieldLength = Short.reverseBytes(buffer.getShort());
      byte[] fieldValue = new byte[fieldLength];
      buffer.get(fieldValue);

      if (fieldCode == code) {
        return new String(fieldValue, UTF_8);
      }
    }

    return null;
  }

  @Value
  @Builder
  @EqualsAndHashCode
  public static class ZippedEntry {

    String path;
    String fullUrl;
    String body;
    Instant instant;
  }
}
