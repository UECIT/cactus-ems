package uk.nhs.ctp.tkwvalidation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.audit.AuditParser;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.testhelper.matchers.FunctionMatcher;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;

@RunWith(MockitoJUnitRunner.class)
public class FhirMessageAuditTransformerTest {

  private static final String FHIR_JSON = "content-type: [application/fhir+json]";
  private static final String FHIR_XML = "content-type: [application/fhir+xml]";
  private static final String JSON_FHIR = "content-type: [application/json+fhir]";
  private static final String XML_FHIR = "content-type: [application/xml+fhir]";

  private static final Instant TIMESTAMP = Instant.parse("2020-07-16T09:33:42Z");
  public static final String REQUEST_URL = "https://host.com/path";

  @Mock
  private AuditParser auditParser;

  @InjectMocks
  private FhirMessageAuditTransformer transformer;

  @Before
  public void setup() {
    when(auditParser.getHeadersFrom(FHIR_JSON))
        .thenReturn(Map.of("content-type", List.of("application/fhir+json")));
    when(auditParser.getHeadersFrom(FHIR_XML))
        .thenReturn(Map.of("content-type", List.of("application/fhir+xml")));
    when(auditParser.getHeadersFrom(JSON_FHIR))
        .thenReturn(Map.of("content-type", List.of("application/json+fhir")));
    when(auditParser.getHeadersFrom(XML_FHIR))
        .thenReturn(Map.of("content-type", List.of("application/xml+fhir")));
  }

  @Test
  public void fromEntry_withFhirJsonHeaders_shouldTransform() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_JSON)
        .responseBody("responseBody")
        .responseHeaders(JSON_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage("requestBody", "responseBody"));
  }

  @Test
  public void fromEntry_withFhirXmlHeaders_shouldTransform() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage("requestBody", "responseBody"));
  }

  @Test
  public void fromEntry_withoutIncludeRequest_shouldReturnNullRequest() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(null, "responseBody"));
  }

  @Test
  public void fromEntry_withoutRequestHeaders_shouldReturnNullRequest() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(null, "responseBody"));
  }

  @Test
  public void fromEntry_withoutIncludeRequestAndResponseHeaders_shouldReturnBothNull() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(null,null));
  }

  @Test
  public void fromEntry_withoutHeaders_shouldReturnBothNull() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(null, null));
  }

  @Test
  public void fromSession_withFhirJsonHeaders_shouldTransform() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_JSON)
        .responseBody("responseBody")
        .responseHeaders(JSON_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage("requestBody", "responseBody"));
  }

  @Test
  public void fromSession_withFhirXmlHeaders_shouldTransform() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage("requestBody", "responseBody"));
  }

  @Test
  public void fromSession_withoutIncludeRequest_shouldReturnNullRequest() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(null, "responseBody"));
  }

  @Test
  public void fromSession_withoutRequestHeaders_shouldReturnNullRequest() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(null, "responseBody"));
  }

  @Test
  public void fromSession_withoutIncludeRequestAndResponseHeaders_shouldReturnBothNull() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl("https://host.com/path")
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(null, null));
  }

  @Test
  public void fromSession_withoutHeaders_shouldReturnBothNull() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl(REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(null, null));
  }

  private Matcher<FhirMessageAudit> isMessage(
      String requestBody,
      String responseBody) {
    return new FunctionMatcher<>(messageAudit ->
        "base/host.com/path".equals(messageAudit.getFilePath())
            && Objects.equals(requestBody, messageAudit.getRequestBody())
            && Objects.equals(responseBody, messageAudit.getResponseBody())
            && TIMESTAMP.equals(messageAudit.getMoment()),
        "is entry with bodies " + requestBody + " and " + responseBody);
  }
}