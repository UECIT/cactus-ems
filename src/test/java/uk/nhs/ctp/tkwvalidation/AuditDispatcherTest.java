package uk.nhs.ctp.tkwvalidation;


import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

@RunWith(MockitoJUnitRunner.class)
public class AuditDispatcherTest {

  @Mock
  AlternativeRestTemplate restTemplate;

  @InjectMocks
  AuditDispatcher auditDispatcher;

  @Test
  public void getValidationUrl() {
    ReflectionTestUtils.setField(auditDispatcher, "reportValidationServer", "validServerUrl");

    assertThat(auditDispatcher.getValidationUrl(), is("validServerUrl/$evaluate"));
  }

  @Test
  public void dispatchToTkw() throws IOException {
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

    when(restTemplate.exchange(isA(RequestEntity.class)))
        .thenReturn(ResponseEntity.accepted().build());

    auditDispatcher.dispatchToTkw(zipData, zipMetadata);

    var requestCaptor = ArgumentCaptor.forClass(RequestEntity.class);
    verify(restTemplate).exchange(requestCaptor.capture());
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