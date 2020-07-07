package uk.nhs.ctp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

  public void addEntry(String path, String body, Instant creationDate) throws IOException {
    ZipEntry zipEntry = new ZipEntry(path);
    zipEntry.setCreationTime(FileTime.from(creationDate));
    zip.putNextEntry(zipEntry);
    zip.write(body.getBytes(StandardCharsets.UTF_8));
  }

  public byte[] buildZip() throws IOException {
    zip.closeEntry();
    zip.close();

    return output.toByteArray();
  }
}
