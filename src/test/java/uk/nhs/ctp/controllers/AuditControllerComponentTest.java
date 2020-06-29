package uk.nhs.ctp.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.nhs.ctp.testhelper.matchers.ClientExceptionMatchers.hasStatusCode;

import java.io.IOException;
import java.util.Collections;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.auditFinder.ElasticSearchClient;
import uk.nhs.ctp.auditFinder.model.AuditValidationRequest;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuditControllerComponentTest {

  private static final String TEST_SUPPLIER_ID = "testSupplierId";

  @MockBean
  private ElasticSearchClient esClient;

  @MockBean
  private TokenAuthenticationService authenticationService;

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
  public void validate_withNoCaseIdAndSearchAuditId_shouldFail() {
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
  public void validate_withCaseId_shouldReturnEncounterAudits() {
  }

  @Test
  public void validate_withSearchAuditId_shouldReturnSearchAudits() {
  }
}