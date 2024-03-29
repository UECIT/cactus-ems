package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.nhs.cactus.common.audit.model.OperationType.ENCOUNTER;
import static uk.nhs.cactus.common.audit.model.OperationType.SERVICE_SEARCH;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.cactus.common.elasticsearch.ElasticSearchClient;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.auditFinder.model.AuditValidationRequest;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.testhelper.AuditUnzipper.ZippedEntry;
import uk.nhs.ctp.tkwvalidation.AlternativeRestTemplate;

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
  private AlternativeRestTemplate restTemplate;

  @Autowired
  private AuditController auditController;

  @Autowired
  private CdssSupplierRepository cdssSupplierRepository;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    when(authenticationService.requireSupplierId()).thenReturn(TEST_SUPPLIER_ID);
  }

  @Test
  public void validate_withNoInteractionId_shouldFail() throws IOException {
    var request = new AuditValidationRequest();
    request.setType(OperationType.CHECK_SERVICES);

    expectedException.expect(hasStatusCode(BAD_REQUEST));
    auditController.validate(request);
  }

  @Test
  public void validate_withNonexistentInteractionId_shouldFail() throws IOException {
    var request = new AuditValidationRequest();
    request.setType(OperationType.IS_VALID);
    request.setInteractionId("nonexistentInteractionId");

    when(esClient.search(anyString(), any(SearchSourceBuilder.class)))
        .thenReturn(Collections.emptyList());

    expectedException.expect(hasStatusCode(NOT_FOUND));
    auditController.validate(request);
  }

  @Test
  public void validate_shouldSendEncounterAudits() throws IOException {
    var request = new AuditValidationRequest();
    request.setInstanceBaseUrl("http://existing.cdss/supplier");
    request.setInteractionId("validInteractionId");
    request.setType(ENCOUNTER);

    when(esClient.search(anyString(), any(SearchSourceBuilder.class)))
        .thenReturn(encounterSearchHits(getClass().getClassLoader()));
    when(restTemplate.exchange(isA(RequestEntity.class)))
        .thenReturn(ResponseEntity.accepted().build());

    var selectedCdss = new CdssSupplier();
    selectedCdss.setName("selectedCdss");
    selectedCdss.setBaseUrl("http://existing.cdss/supplier");
    selectedCdss.setSupportedVersion(CdsApiVersion.ONE_ONE);
    cdssSupplierRepository.saveAndFlush(selectedCdss);

    auditController.validate(request);

    var requestCaptor = ArgumentCaptor.forClass(RequestEntity.class);
    verify(restTemplate).exchange(requestCaptor.capture());
    var tkwRequest = requestCaptor.getValue();

    var decodedBytes = Base64.getDecoder().decode((byte[])tkwRequest.getBody());

    var entry1 = ZippedEntry.builder()
        .path("encounter91/unknown-host/case/.1.request.xml")
        .fullUrl("http://unknown-host/case/")
        .body("auditRequestBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry2 = ZippedEntry.builder()
        .path("encounter91/unknown-host/case/.1.response.xml")
        .fullUrl("http://unknown-host/case/")
        .body("auditResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry3 = ZippedEntry.builder()
        .path("encounter91/cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2.1.response.xml")
        .fullUrl("https://cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:32Z"))
        .build();
    var entry4 = ZippedEntry.builder()
        .path("encounter91/fhir-server.cactus-staging.iucdspilot.uk/fhir/QuestionnaireResponse/849.1.response.xml")
        .fullUrl("http://fhir-server.cactus-staging.iucdspilot.uk/fhir/QuestionnaireResponse/849")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:33Z"))
        .build();

    assertThat(unzipEntries(decodedBytes), contains(entry1, entry2, entry3, entry4));
  }

  @Test
  public void download_withCaseId_shouldReturnZip() throws IOException {
    var request = new AuditValidationRequest();
    request.setInteractionId("validCaseId");
    request.setInstanceBaseUrl("http://existing.cdss/supplier");
    request.setType(OperationType.ENCOUNTER);

    when(esClient.search(anyString(), any(SearchSourceBuilder.class)))
        .thenReturn(encounterSearchHits(getClass().getClassLoader()));

    var selectedCdss = new CdssSupplier();
    selectedCdss.setName("selectedCdss");
    selectedCdss.setBaseUrl("http://existing.cdss/supplier");
    selectedCdss.setSupportedVersion(CdsApiVersion.ONE_ONE);
    cdssSupplierRepository.saveAndFlush(selectedCdss);

    ResponseEntity<byte[]> response = auditController.download(request);

    var responseBytes = response.getBody();

    var entry1 = ZippedEntry.builder()
        .path("encounter91/unknown-host/case/.1.request.xml")
        .fullUrl("http://unknown-host/case/")
        .body("auditRequestBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry2 = ZippedEntry.builder()
        .path("encounter91/unknown-host/case/.1.response.xml")
        .fullUrl("http://unknown-host/case/")
        .body("auditResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry3 = ZippedEntry.builder()
        .path("encounter91/cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2.1.response.xml")
        .fullUrl("https://cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:32Z"))
        .build();
    var entry4 = ZippedEntry.builder()
        .path("encounter91/fhir-server.cactus-staging.iucdspilot.uk/fhir/QuestionnaireResponse/849.1.response.xml")
        .fullUrl("http://fhir-server.cactus-staging.iucdspilot.uk/fhir/QuestionnaireResponse/849")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:33Z"))
        .build();

    assertThat(unzipEntries(responseBytes), contains(entry1, entry2, entry3, entry4));
  }

  @Test
  public void validate_shouldSendSearchAudits() throws IOException {
    var request = new AuditValidationRequest();
    request.setInstanceBaseUrl("http://non-existing.cdss/supplier");
    request.setInteractionId("validInteractionId");
    request.setType(SERVICE_SEARCH);

    when(esClient.search(anyString(), any(SearchSourceBuilder.class)))
        .thenReturn(serviceDefinitionSearchHits(getClass().getClassLoader()));
    when(restTemplate.exchange(isA(RequestEntity.class)))
        .thenReturn(ResponseEntity.accepted().build());

    auditController.validate(request);

    var requestCaptor = ArgumentCaptor.forClass(RequestEntity.class);
    verify(restTemplate).exchange(requestCaptor.capture());
    var tkwRequest = requestCaptor.getValue();

    var decodedBytes = Base64.getDecoder().decode((byte[])tkwRequest.getBody());

    var entry1 = ZippedEntry.builder()
        .path("service_searchvalidGuidValue/unknown-host/case/serviceDefinitions.1.response.xml")
        .fullUrl("http://unknown-host/case/serviceDefinitions")
        .body("auditResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:31Z"))
        .build();
    var entry2 = ZippedEntry.builder()
        .path("service_searchvalidGuidValue/cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2.1.response.xml")
        .fullUrl("https://cdss.cactus-staging.iucdspilot.uk/fhir/ServiceDefinition/palpitations2")
        .body("entry1ResponseBody")
        .instant(Instant.parse("2020-06-30T07:56:32Z"))
        .build();

    assertThat(unzipEntries(decodedBytes), contains(entry1, entry2));
  }

  @SuppressWarnings("unused")
  private static void saveZipToTempFile(byte[] zipData) throws IOException {
    File zipFile = File.createTempFile("validation", ".zip");

    try (FileOutputStream outputStream = new FileOutputStream(zipFile)) {
      outputStream.write(zipData);
    }

    System.out.println("Output written to " + zipFile);
  }
}