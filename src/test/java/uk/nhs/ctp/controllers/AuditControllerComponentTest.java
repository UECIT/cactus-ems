package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.nhs.ctp.testhelper.AuditUnzipper.unzipEntries;
import static uk.nhs.ctp.testhelper.fixtures.ElasticSearchFixtures.encounterSearchHits;
import static uk.nhs.ctp.testhelper.fixtures.ElasticSearchFixtures.serviceDefinitionSearchHits;
import static uk.nhs.ctp.testhelper.matchers.ClientExceptionMatchers.hasStatusCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.auditFinder.ElasticSearchClient;
import uk.nhs.ctp.auditFinder.model.AuditValidationRequest;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.testhelper.AuditUnzipper.ZippedEntry;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("default")
public class AuditControllerComponentTest {

  private static final String TEST_SUPPLIER_ID = "testSupplierId";

  @MockBean
  private ElasticSearchClient esClient;

  @MockBean
  private TokenAuthenticationService authenticationService;

  @MockBean
  @Qualifier("restTemplate")
  private RestTemplate restTemplate;

  @Autowired
  private AuditController auditController;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    when(authenticationService.requireSupplierId())
        .thenReturn(TEST_SUPPLIER_ID);
  }

  @Test
  public void validate_withNoCaseIdAndSearchAuditId_shouldFail() throws IOException {
    var request = new AuditValidationRequest();
    request.setCaseId(null);
    request.setSearchAuditId("");

    expectedException.expect(hasStatusCode(BAD_REQUEST));
    auditController.validate(request);
  }

  @Test
  public void validate_withNonexistentSearchAuditId_shouldFail() throws IOException {
    var request = new AuditValidationRequest();
    request.setCaseId(null);
    request.setSearchAuditId("nonexistentSearchAuditId");

    when(esClient.search(anyString(), any(SearchSourceBuilder.class)))
        .thenReturn(Collections.emptyList());

    expectedException.expect(hasStatusCode(NOT_FOUND));
    auditController.validate(request);
  }

  @Test
  public void validate_withCaseId_shouldSendEncounterAudits() throws IOException {
    var request = new AuditValidationRequest();
    request.setCaseId("validCaseId");
    request.setSearchAuditId(null);
    request.setType(OperationType.ENCOUNTER);

    when(esClient.search(anyString(), any(SearchSourceBuilder.class)))
        .thenReturn(encounterSearchHits(getClass().getClassLoader()));
    when(restTemplate.exchange(isA(RequestEntity.class), argThat(sameInstance(String.class))))
        .thenReturn(ResponseEntity.ok(VALIDATION_RESPONSE));

    var result = auditController.validate(request);

    assertThat(result, is("validDiagnosticsHtml"));

    var requestCaptor = ArgumentCaptor.forClass(RequestEntity.class);
    verify(restTemplate).exchange(requestCaptor.capture(), argThat(sameInstance(String.class)));
    var tkwRequest = requestCaptor.getValue();

    var decodedBytes = Base64.getDecoder().decode((byte[])tkwRequest.getBody());

    var entry1 = ZippedEntry.builder()
        .path("encounter91/null/case/.1.request.xml")
        .body("auditRequestBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry2 = ZippedEntry.builder()
        .path("encounter91/null/case/.1.response.xml")
        .body("auditResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry3 = ZippedEntry.builder()
        .path("encounter91/cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2.1.response.xml")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:32Z"))
        .build();
    var entry4 = ZippedEntry.builder()
        .path("encounter91/fhir-server.cactus-staging.iucdspilot.uk/fhir/QuestionnaireResponse/849.1.response.xml")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:33Z"))
        .build();

    assertThat(unzipEntries(decodedBytes), contains(entry1, entry2, entry3, entry4));
  }

  @Test
  public void validate_withSearchAuditId_shouldSendSearchAudits() throws IOException {
    var request = new AuditValidationRequest();
    request.setCaseId(null);
    request.setSearchAuditId("validSearchAuditId");
    request.setType(OperationType.SERVICE_SEARCH);

    when(esClient.search(anyString(), any(SearchSourceBuilder.class)))
        .thenReturn(serviceDefinitionSearchHits(getClass().getClassLoader()));
    when(restTemplate.exchange(isA(RequestEntity.class), argThat(sameInstance(String.class))))
        .thenReturn(ResponseEntity.ok(VALIDATION_RESPONSE));

    var result = auditController.validate(request);

    assertThat(result, is("validDiagnosticsHtml"));

    var requestCaptor = ArgumentCaptor.forClass(RequestEntity.class);
    verify(restTemplate).exchange(requestCaptor.capture(), argThat(sameInstance(String.class)));
    var tkwRequest = requestCaptor.getValue();

    var decodedBytes = Base64.getDecoder().decode((byte[])tkwRequest.getBody());

    var entry1 = ZippedEntry.builder()
        .path("service_search/null/case/serviceDefinitions.1.response.xml")
        .body("auditResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry2 = ZippedEntry.builder()
        .path("service_search/cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2.1.response.xml")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:32Z"))
        .build();

    assertThat(unzipEntries(decodedBytes), contains(entry1, entry2));
  }

  private static final String VALIDATION_RESPONSE = "{"
      + "    \"resourceType\": \"OperationOutcome\","
      + "    \"issue\": [{"
      + "        \"diagnostics\": \"validDiagnosticsHtml\""
      + "    }]"
      + "}";

  private static void saveZipToTempFile(byte[] zipData) throws IOException {
    File zipFile = File.createTempFile("validation", ".zip");

    try (FileOutputStream outputStream = new FileOutputStream(zipFile)) {
      outputStream.write(zipData);
    }

    System.out.println("Output written to " + zipFile);
  }
}