package uk.nhs.ctp.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.finder.AuditFinder;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceTest {

  @Mock
  AuditFinder auditFinder;

  @InjectMocks
  ValidationService validationService;

  @Test
  public void zip_creation() throws IOException {
    var audit = AuditSession.builder()
        .entry(AuditEntry.builder()
            .requestMethod("GET")
            .requestUrl("http://fhir.server/fhir/Encounter/5")
            .dateOfEntry(Instant.now())
            .responseBody("Encounter resource")
            .build())
        .build();

    when(auditFinder.findAll(anyLong()))
        .thenReturn(List.of(audit));

    byte[] output = validationService.zipResources(1L);
    assertNotNull(output);
    assertTrue(output.length > 0);

//    File zipFile = File.createTempFile("validation", ".zip");
//    try (FileOutputStream outputStream = new FileOutputStream(zipFile)) {
//      outputStream.write(output);
//    }
//    System.out.println("Output written to " + zipFile);
  }
}