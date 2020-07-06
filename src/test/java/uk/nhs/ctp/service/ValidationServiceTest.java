package uk.nhs.ctp.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.Builder;
import lombok.Value;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.testhelper.matchers.FunctionMatcher;

public class ValidationServiceTest {
  // Intentionally set without milliseconds as the zipping process truncates
  // creation date-times to second precision
  private static final Instant VALID_CREATION_INSTANT_1 = Instant.parse("2020-07-06T10:23:31Z");
  private static final Instant VALID_CREATION_INSTANT_2 = Instant.parse("2019-06-05T09:12:20Z");

  private final ValidationService validationService = new ValidationService();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void zip_creation() throws IOException {
    var audit = AuditSession.builder()
        .entry(AuditEntry.builder()
            .requestMethod("GET")
            .requestUrl("http://fhir.server/fhir/Encounter/5")
            .dateOfEntry(VALID_CREATION_INSTANT_1)
            .responseBody("Encounter resource")
            .build())
        .additionalProperty("caseId", "6")
        .build();

    byte[] output = validationService.zipAudits(List.of(audit), OperationType.ENCOUNTER);
    assertNotNull(output);
    assertTrue(output.length > 0);

//    File zipFile = File.createTempFile("validation", ".zip");
//    try (FileOutputStream outputStream = new FileOutputStream(zipFile)) {
//      outputStream.write(output);
//    }
//    System.out.println("Output written to " + zipFile);
  }

  @Test
  public void zipAudits_withMoreThan1ServiceSearchAudit_shouldFail() throws IOException {
    var audits = List.of(AuditSession.builder().build(), AuditSession.builder().build());

    expectedException.expect(UnsupportedOperationException.class);
    validationService.zipAudits(audits, OperationType.SERVICE_SEARCH);
  }

  @Test
  public void zipAudits_withMoreThan1ServiceSearchAuditEntry_shouldFail() throws IOException {
    var audits = List.of(AuditSession.builder()
        .entry(AuditEntry.builder().build())
        .entry(AuditEntry.builder().build())
        .build());

    expectedException.expect(UnsupportedOperationException.class);
    validationService.zipAudits(audits, OperationType.SERVICE_SEARCH);
  }

  @Test
  public void zipAudits_withEncountersLackingCaseId_shouldFail() throws IOException {
    var audits = List.of(
        AuditSession.builder().build(),
        AuditSession.builder().additionalProperty("caseId", "validCaseId").build());

    expectedException.expect(UnsupportedOperationException.class);
    validationService.zipAudits(audits, OperationType.ENCOUNTER);
  }

  @Test
  public void zipAudits_withEncounterLackingCaseId_shouldFail() throws IOException {
    var audits = List.of(AuditSession.builder().build());

    expectedException.expect(UnsupportedOperationException.class);
    validationService.zipAudits(audits, OperationType.ENCOUNTER);
  }

  @Test
  public void zipAudits_shouldOnlyZipGetEntries() throws IOException {
    var getEntry = AuditEntry.builder()
        .requestMethod("GET")
        .dateOfEntry(VALID_CREATION_INSTANT_1)
        .requestUrl("http://valid.com/request/url")
        .responseBody("{}")
        .build();
    var audits = List.of(AuditSession.builder()
        .entry(AuditEntry.builder().requestMethod("POST").build())
        .entry(AuditEntry.builder().requestMethod("PUT").build())
        .entry(AuditEntry.builder().requestMethod("DELETE").build())
        .entry(getEntry)
        .additionalProperty("caseId", "33")
        .build());

    var zippedAudits = validationService.zipAudits(audits, OperationType.ENCOUNTER);

    assertThat(unzipEntries(zippedAudits), hasSize(1));
  }

  @Test
  public void zipAudits_withEncounterAudits_shouldZipEntryData() throws IOException {
    var entry1 = AuditEntry.builder()
        .requestMethod("GET")
        .responseBody("{ \"body\": \"validRequestBody1\" }")
        .requestUrl("http://valid.com/request/url1")
        .dateOfEntry(VALID_CREATION_INSTANT_1)
        .build();
    var entry2 = AuditEntry.builder()
        .requestMethod("GET")
        .responseBody("<body>validRequestBody2</body>")
        .requestUrl("http://valid.com/request/url2")
        .dateOfEntry(VALID_CREATION_INSTANT_2)
        .build();
    var entry3 = AuditEntry.builder()
        .requestMethod("GET")
        .responseBody("{ \"body\": \"validRequestBody3\" }")
        .requestUrl("http://valid.com/request/url3")
        .dateOfEntry(VALID_CREATION_INSTANT_1)
        .build();

    var audit1 = AuditSession.builder()
        .entry(entry1)
        .entry(entry2)
        .additionalProperty("caseId", "11")
        .build();
    var audit2 = AuditSession.builder()
        .entry(entry3)
        .additionalProperty("caseId", "22")
        .build();

    var audits = List.of(audit1, audit2);

    var zipped = validationService.zipAudits(audits, OperationType.ENCOUNTER);

    assertThat(unzipEntries(zipped), containsInAnyOrder(
        matchesAuditEntry("encounter11/valid.com/request/url1.json", entry1),
        matchesAuditEntry("encounter11/valid.com/request/url2.xml", entry2),
        matchesAuditEntry("encounter22/valid.com/request/url3.json", entry3)
    ));
  }

  @Test
  public void zipAudits_withServiceSearchAudits_shouldZipEntryData() throws IOException {
    var entry = AuditEntry.builder()
        .requestMethod("GET")
        .responseBody("{ \"body\": \"validRequestBody1\" }")
        .requestUrl("http://valid.com/request/url1")
        .dateOfEntry(VALID_CREATION_INSTANT_2)
        .build();

    var audits = singletonList(AuditSession.builder().entry(entry).build());

    var zipped = validationService.zipAudits(audits, OperationType.SERVICE_SEARCH);

    assertThat(unzipEntries(zipped), contains(
        matchesAuditEntry("service_search/valid.com/request/url1.json", entry)
    ));
  }

  private List<ZippedAuditEntry> unzipEntries(byte[] bytes) throws IOException {
    try (var input = new ByteArrayInputStream(bytes)) {
      try (var zip = new ZipInputStream(input)) {
        var entries = new ArrayList<ZippedAuditEntry>();

        ZipEntry zipEntry;
        while ((zipEntry = zip.getNextEntry()) != null) {
          entries.add(ZippedAuditEntry.builder()
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
  private static class ZippedAuditEntry {
    String path;
    String body;
    Instant instant;
  }

  private Matcher<ZippedAuditEntry> matchesAuditEntry(String path, AuditEntry auditEntry) {
    return new FunctionMatcher<>(
        zipEntry -> path.equals(zipEntry.getPath())
            && auditEntry.getDateOfEntry().equals(zipEntry.getInstant())
            && auditEntry.getResponseBody().equals(zipEntry.getBody()),
        "matches auditEntry on path " + path);
  }
}