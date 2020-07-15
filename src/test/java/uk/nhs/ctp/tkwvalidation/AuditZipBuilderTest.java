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
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;

@RunWith(MockitoJUnitRunner.class)
public class AuditZipBuilderTest {
  private static final Instant CREATED_AT_1 = Instant.parse("2020-07-06T10:23:31Z");
  private static final Instant CREATED_AT_2 = Instant.parse("2019-06-05T09:12:20Z");

  private ZipBuilder zipBuilder;

  @Mock
  private ZipBuilderFactory zipBuilderFactory;

  @InjectMocks
  private AuditZipBuilder auditZipBuilder;

  @Before
  public void setup() {
    zipBuilder = mock(ZipBuilder.class);
    when(zipBuilderFactory.create()).thenReturn(zipBuilder);
  }

  @Test
  public void zipAudits_withNoAudits_shouldReturnEmpty() throws IOException {
    var expectedZipData = new byte[]{};
    when(zipBuilder.buildAndCloseZip()).thenReturn(expectedZipData);

    var zipData = auditZipBuilder.zipMessageAudits(emptyList());

    verify(zipBuilder, never()).addEntry(anyString(), anyString(), any(Instant.class));
    assertThat(zipData, is(expectedZipData));
  }

  @Test
  public void zipAudits_shouldReturnUnmodifiedZip() throws IOException {
    var expectedZipData = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
    when(zipBuilder.buildAndCloseZip()).thenReturn(expectedZipData);

    var selectedAudits = Collections.singletonList(
        FhirMessageAudit.builder()
            .filePath("path")
            .responseBody("responseBody1")
            .moment(CREATED_AT_1).build());
    var zipData = auditZipBuilder.zipMessageAudits(selectedAudits);

    assertThat(zipData, is(expectedZipData));
  }

  @Test
  public void zipAudits_shouldIncrementSequenceCounts() throws IOException {
    var selectedAudits = List.of(
        FhirMessageAudit.builder()
            .filePath("path")
            .responseBody("responseBody1")
            .moment(CREATED_AT_1).build(),
        FhirMessageAudit.builder()
            .filePath("path")
            .requestBody("requestBody2")
            .responseBody("responseBody2")
            .moment(CREATED_AT_2).build());

    auditZipBuilder.zipMessageAudits(selectedAudits);

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
    var selectedAudits = List.of(
        FhirMessageAudit.builder()
            .filePath("jsonPath")
            .responseBody("{ \"a\": \"b\" }")
            .moment(CREATED_AT_1).build(),
        FhirMessageAudit.builder()
            .filePath("xmlPath")
            .responseBody("<a>b</a>")
            .moment(CREATED_AT_1).build(),
        FhirMessageAudit.builder()
            .filePath("emptyPath")
            .responseBody("")
            .moment(CREATED_AT_1).build(),
        FhirMessageAudit.builder()
            .filePath("undefinedPath")
            .responseBody("Base64==")
            .moment(CREATED_AT_1).build());

    auditZipBuilder.zipMessageAudits(selectedAudits);

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