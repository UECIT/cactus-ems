package uk.nhs.ctp.tkwvalidation;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.tkwvalidation.models.HttpMessageAudit;
import uk.nhs.ctp.tkwvalidation.rules.AuditValidationRule;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceTest {
  private static final Instant CREATED_AT_1 = Instant.parse("2020-07-06T10:23:31Z");
  private static final Instant CREATED_AT_2 = Instant.parse("2019-06-05T09:12:20Z");

  private ZipBuilder zipBuilder;

  @Mock
  private ZipBuilderFactory zipBuilderFactory;

  @Mock
  private AuditSelector auditSelector;

  @Mock
  private Map<String, AuditValidationRule> validationRules;

  @InjectMocks
  private ValidationService validationService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    zipBuilder = mock(ZipBuilder.class);
    when(zipBuilderFactory.create()).thenReturn(zipBuilder);
  }

  @Test
  public void zip_creation() throws IOException {
    var audit = AuditSession.builder()
        .entry(AuditEntry.builder()
            .requestMethod("GET")
            .requestUrl("http://fhir.server/fhir/Encounter/5")
            .dateOfEntry(CREATED_AT_1)
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
  public void zipAudits_shouldEnsureValidationRules() throws IOException {
    var audits = new ArrayList<AuditSession>();
    var rule = mock(AuditValidationRule.class);
    when(validationRules.get("encounter")).thenReturn(mock(AuditValidationRule.class));

    validationService.zipAudits(audits, OperationType.ENCOUNTER);

    verify(rule).ensure(audits);
  }

  @Test
  public void zipAudits_withNoAudits_shouldReturnEmpty() throws IOException {
    var expectedZipData = new byte[]{};
    when(zipBuilder.buildAndCloseZip()).thenReturn(expectedZipData);

    var zipData = validationService.zipAudits(emptyList(), OperationType.ENCOUNTER);

    verify(zipBuilder, never()).addEntry(anyString(), anyString(), any(Instant.class));
    assertThat(zipData, is(expectedZipData));
  }

  @Test
  public void zipAudits_shouldReturnUnmodifiedZip() throws IOException {
    var expectedZipData = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
    when(zipBuilder.buildAndCloseZip()).thenReturn(expectedZipData);

    var entry = AuditEntry.builder()
        .requestMethod("GET")
        .dateOfEntry(CREATED_AT_1)
        .requestUrl("http://valid.com/request/url1")
        .responseBody("{}")
        .build();
    var audits = List.of(AuditSession.builder()
        .entry(entry)
        .build());
    var zipData = validationService.zipAudits(audits, OperationType.SERVICE_SEARCH);

    assertThat(zipData, is(expectedZipData));
  }

  @Test
  public void zipAudits_shouldIncrementSequenceCounts() throws IOException {
    when(validationRules.get("encounter")).thenReturn(mock(AuditValidationRule.class));

    var initialAudits = new ArrayList<AuditSession>();
    var selectedAudits = List.of(
        HttpMessageAudit.builder()
            .filePath("path")
            .responseBody("responseBody1")
            .moment(CREATED_AT_1).build(),
        HttpMessageAudit.builder()
            .filePath("path")
            .requestBody("requestBody2")
            .responseBody("responseBody2")
            .moment(CREATED_AT_2).build());
    when(auditSelector.selectAudits(initialAudits, OperationType.ENCOUNTER))
        .thenReturn(selectedAudits);

    validationService.zipAudits(initialAudits, OperationType.ENCOUNTER);

    verify(zipBuilder).addEntry(
        "path.1.response.xml",
        "responseBody1",
        CREATED_AT_1);
    verify(zipBuilder).addEntry(
        "path.2.request.xml",
        "requestBody2",
        CREATED_AT_2);
    verify(zipBuilder).addEntry(
        "path.2.response.xml",
        "responseBody2",
        CREATED_AT_2);
  }

  @Test
  public void zipAudits_shouldNaivelyIdentifyContentType() throws IOException {
    when(validationRules.get("encounter")).thenReturn(mock(AuditValidationRule.class));

    var initialAudits = new ArrayList<AuditSession>();
    var selectedAudits = List.of(
        HttpMessageAudit.builder()
            .filePath("jsonPath")
            .responseBody("{ \"a\": \"b\" }")
            .moment(CREATED_AT_1).build(),
        HttpMessageAudit.builder()
            .filePath("xmlPath")
            .responseBody("<a>b</a>")
            .moment(CREATED_AT_1).build(),
        HttpMessageAudit.builder()
            .filePath("emptyPath")
            .responseBody("")
            .moment(CREATED_AT_1).build(),
        HttpMessageAudit.builder()
            .filePath("undefinedPath")
            .responseBody("Base64==")
            .moment(CREATED_AT_1).build());
    when(auditSelector.selectAudits(initialAudits, OperationType.ENCOUNTER))
        .thenReturn(selectedAudits);

    validationService.zipAudits(initialAudits, OperationType.ENCOUNTER);

    verify(zipBuilder).addEntry(
        "jsonPath.1.response.json",
        "{ \"a\": \"b\" }",
        CREATED_AT_1);
    verify(zipBuilder).addEntry(
        "xmlPath.1.response.xml",
        "<a>b</a>",
        CREATED_AT_1);
    verify(zipBuilder).addEntry(
        "emptyPath.1.response.xml",
        "",
        CREATED_AT_1);
    verify(zipBuilder).addEntry(
        "undefinedPath.1.response.xml",
        "Base64==",
        CREATED_AT_1);
  }
}