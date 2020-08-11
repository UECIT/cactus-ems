package uk.nhs.ctp.tkwvalidation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipBuilder {

  private final ByteArrayOutputStream output;
  private final ZipOutputStream zip;

  public ZipBuilder() {
    output = new ByteArrayOutputStream();
    zip = new ZipOutputStream(output);
  }

  public void addEntry(String path, String fullUrl, String body, Instant creationDate)
      throws IOException {
    ZipEntry zipEntry = new ZipEntry(path);

    byte[] fullPathBytes = fullUrl.getBytes(UTF_8);
    ByteBuffer extra = ByteBuffer.allocate(fullPathBytes.length + 4);
    extra.putShort((short) 0x0707);
    extra.putShort(Short.reverseBytes((short) fullPathBytes.length));
    extra.put(fullPathBytes);

    zipEntry.setExtra(extra.array());
    zipEntry.setCreationTime(FileTime.from(creationDate));
    zip.putNextEntry(zipEntry);
    zip.write(body.getBytes(UTF_8));
  }

  public byte[] buildAndCloseZip() throws IOException {
    zip.closeEntry();
    zip.close();

    return output.toByteArray();
  }
}
