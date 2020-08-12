package uk.nhs.ctp.tkwvalidation;

import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.util.Objects;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.audit.AuditParser;
import uk.nhs.cactus.common.audit.model.AuditEntry;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.ctp.testhelper.matchers.FunctionMatcher;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;

@RunWith(MockitoJUnitRunner.class)
public class FhirMessageAuditTransformerTest {

  private static final String FHIR_JSON = "content-type: [application/fhir+json]\nhost: [host.com]";
  private static final String FHIR_XML = "content-type: [application/fhir+xml]\nhost: [host.com]";
  private static final String JSON_FHIR = "content-type: [application/json+fhir]\nhost: [host.com]";
  private static final String XML_FHIR = "content-type: [application/xml+fhir]\nhost: [host.com]";

  private static final Instant TIMESTAMP = Instant.parse("2020-07-16T09:33:42Z");
  public static final String FILE_PATH_WITH_HOST = "base/host.com/path";
  public static final String FILE_PATH_WITH_UNKNWON_HOST = "base/unknown-host/path";
  public static final String ABSOLUTE_REQUEST_URL = "https://host.com/path";
  public static final String RELATIVE_REQUEST_URL = "/path";

  private FhirMessageAuditTransformer transformer;

  @Before
  public void setup() {
    transformer = new FhirMessageAuditTransformer(new AuditParser());
  }

  @Test
  public void fromEntry_withFhirJsonHeaders_shouldTransform() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl(ABSOLUTE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_JSON)
        .responseBody("responseBody")
        .responseHeaders(JSON_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, "requestBody", "responseBody"));
  }

  @Test
  public void fromEntry_withFhirXmlHeaders_shouldTransform() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl(ABSOLUTE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, "requestBody", "responseBody"));
  }

  @Test
  public void fromEntry_withoutIncludeRequest_shouldReturnNullRequest() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl(ABSOLUTE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, null, "responseBody"));
  }

  @Test
  public void fromEntry_withoutRequestHeaders_shouldReturnNullRequest() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl(ABSOLUTE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, null, "responseBody"));
  }

  @Test
  public void fromEntry_withoutIncludeRequestAndResponseHeaders_shouldReturnBothNull() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl(ABSOLUTE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, null, null));
  }

  @Test
  public void fromEntry_withoutHeaders_shouldReturnBothNull() {
    var entry = AuditEntry.builder()
        .dateOfEntry(TIMESTAMP)
        .requestUrl(ABSOLUTE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, null, null));
  }

  @Test
  public void fromSession_withFhirJsonHeaders_shouldTransform() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl(RELATIVE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_JSON)
        .responseBody("responseBody")
        .responseHeaders(JSON_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, "requestBody", "responseBody"));
  }

  @Test
  public void fromSession_withFhirXmlHeaders_shouldTransform() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl(RELATIVE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, "requestBody", "responseBody"));
  }

  @Test
  public void fromSession_withoutIncludeRequest_shouldReturnNullRequest() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl(RELATIVE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, null, "responseBody"));
  }

  @Test
  public void fromSession_withoutRequestHeaders_shouldReturnNullRequest() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl(RELATIVE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders(XML_FHIR)
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_UNKNWON_HOST, null, "responseBody"));
  }

  @Test
  public void fromSession_withoutIncludeRequestAndResponseHeaders_shouldReturnBothNull() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl(RELATIVE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders(FHIR_XML)
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", false);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_HOST, null, null));
  }

  @Test
  public void fromSession_withoutHeaders_shouldReturnBothNull() {
    var entry = AuditSession.builder()
        .createdDate(TIMESTAMP)
        .requestUrl(RELATIVE_REQUEST_URL)
        .requestBody("requestBody")
        .requestHeaders("")
        .responseBody("responseBody")
        .responseHeaders("")
        .build();

    var messageAudit = transformer.from(entry, "base", true);

    assertThat(messageAudit, isMessage(FILE_PATH_WITH_UNKNWON_HOST, null, null));
  }

  private Matcher<FhirMessageAudit> isMessage(
      String filePath,
      String requestBody,
      String responseBody) {
    return new FunctionMatcher<>(messageAudit ->
        filePath.equals(messageAudit.getFilePath())
            && Objects.equals(requestBody, messageAudit.getRequestBody())
            && Objects.equals(responseBody, messageAudit.getResponseBody())
            && TIMESTAMP.equals(messageAudit.getMoment()),
        "is entry with filePath " + filePath + " and bodies " + requestBody + " and "
            + responseBody);
  }
}