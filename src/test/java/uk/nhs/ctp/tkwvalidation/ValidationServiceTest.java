package uk.nhs.ctp.tkwvalidation;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.tkwvalidation.model.HttpMessageAudit;
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

  @Before
  public void setup() {
    zipBuilder = mock(ZipBuilder.class);
    when(zipBuilderFactory.create()).thenReturn(zipBuilder);
  }

  @Test
  public void zipAudits_shouldEnsureValidationRules() throws IOException {
    var audits = new ArrayList<AuditSession>();
    var rule = mock(AuditValidationRule.class);
    when(validationRules.get("encounter")).thenReturn(rule);

    validationService.zipAudits(audits, OperationType.ENCOUNTER);

    verify(rule).ensure(audits);
  }

  @Test
  public void zipAudits_withNoAudits_shouldReturnEmpty() throws IOException {
    when(validationRules.get("encounter")).thenReturn(mock(AuditValidationRule.class));
    var expectedZipData = new byte[]{};
    when(zipBuilder.buildAndCloseZip()).thenReturn(expectedZipData);

    var zipData = validationService.zipAudits(emptyList(), OperationType.ENCOUNTER);

    verify(zipBuilder, never()).addEntry(anyString(), anyString(), any(Instant.class));
    assertThat(zipData, is(expectedZipData));
  }

  @Test
  public void zipAudits_shouldReturnUnmodifiedZip() throws IOException {
    when(validationRules.get("service_search")).thenReturn(mock(AuditValidationRule.class));
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