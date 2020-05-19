package uk.nhs.ctp.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.entities.Audit;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.repos.AuditRepository;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceTest {

  @Mock
  AuditRepository auditRepository;

  @InjectMocks
  ValidationService validationService;

  @Test
  public void zip_creation() throws IOException {
    Audit audit = new Audit();
    audit.setAuditEntries(List.of(AuditEntry.builder()
        .requestMethod("GET")
        .requestUrl("http://fhir.server/fhir/Encounter/5")
        .createdDate(new Date())
        .responseBody("Encounter resource")
        .build()));

    when(auditRepository.findAllByCaseIdEqualsAndSuppliedIdEquals(anyLong(), any()))
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