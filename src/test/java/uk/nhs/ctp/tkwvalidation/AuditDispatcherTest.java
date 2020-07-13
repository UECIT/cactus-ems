package uk.nhs.ctp.tkwvalidation;


import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.time.Instant;
import java.util.Base64;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

@RunWith(MockitoJUnitRunner.class)
public class AuditDispatcherTest {

  IParser jsonParser;

  @Mock
  FhirContext fhirContext;

  @Mock
  RestTemplate restTemplate;

  @InjectMocks
  AuditDispatcher auditDispatcher;

  @Before
  public void setup() {
    jsonParser = mock(IParser.class);
    when(fhirContext.newJsonParser()).thenReturn(jsonParser);
  }

  @Test
  public void getValidationUrl() {
    ReflectionTestUtils.setField(auditDispatcher, "reportValidationServer", "validServerUrl");

    assertThat(auditDispatcher.getValidationUrl(), is("validServerUrl/$evaluate"));
  }

  @Test
  public void dispatchToTkw() {
    ReflectionTestUtils.setField(auditDispatcher, "reportValidationServer", "http://validServerUrl.com");
    var fixedDate = Instant.parse("2020-07-06T10:23:31Z");

    var zipData = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
    var zipMetadata = AuditMetadata.builder()
        .supplierId("validSupplierId")
        .apiVersion(CdsApiVersion.TWO)
        .serviceEndpoint("validServiceUrl")
        .interactionType(OperationType.ENCOUNTER)
        .interactionId("validEncounterId")
        .interactionDate(fixedDate)
        .build();

    var issue = new OperationOutcomeIssueComponent();
    issue.setDiagnostics("validDiagnosticsHtml");

    var outcome = new OperationOutcome();
    outcome.addIssue(issue);

    when(restTemplate.exchange(isA(RequestEntity.class), argThat(sameInstance(String.class))))
        .thenReturn(ResponseEntity.ok("validResponse"));
    when(jsonParser.parseResource(OperationOutcome.class, "validResponse"))
        .thenReturn(outcome);

    var response = auditDispatcher.dispatchToTkw(zipData, zipMetadata);

    assertThat(response, is("validDiagnosticsHtml"));

    var requestCaptor = ArgumentCaptor.forClass(RequestEntity.class);
    verify(restTemplate).exchange(requestCaptor.capture(), argThat(sameInstance(String.class)));
    var request = requestCaptor.getValue();

    assertThat(request.getUrl().toString(), is("http://validServerUrl.com/$evaluate"));
    assertThat(request.getBody(), is(Base64.getEncoder().encode(zipData)));
    assertThat(request.getHeaders(), allOf(
        hasEntry("Content-Transfer-Encoding", singletonList("base64")),
        hasEntry("Cactus-Supplier-ID", singletonList("validSupplierId")),
//        hasEntry("Cactus-API-Version", singletonList("1.1")),
        hasEntry("Cactus-Interaction-Type", singletonList("encounter")),
        //hasEntry("Cactus-Interaction-Id", singletonList("validEncounterId")),
        hasEntry("Cactus-Date", singletonList("2020-07-06T10:23:31Z")),
        hasEntry("Cactus-Service-Endpoint", singletonList("validServiceUrl"))));

  }
}