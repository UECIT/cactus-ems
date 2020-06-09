package uk.nhs.ctp.auditFinder;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;

@RunWith(MockitoJUnitRunner.class)
public class AuditFinderServiceTest {

  @Mock
  private RestHighLevelClient restHighLevelClient;

  @Mock
  private TokenAuthenticationService tokenAuthenticationService;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private AuditFinderService auditFinder;

  @Test
  public void findAll_withNullClient_shouldWorkForNow() throws IOException {
    // TODO CDSCT-164: require non-empty ES client
    var auditFinder = new AuditFinderService(
        null,
        mock(TokenAuthenticationService.class),
        mock(ObjectMapper.class));

    var audits = auditFinder.findAll(1L);

    assertThat(audits, is(empty()));
  }


  // TODO: upgrade to Mockito 2 in order to test this further
  // cannot currently test fully due to the RestHighLevelClient using final methods
  // also cannot meaningfully extract generic client logic to a new interface

//  @Test
//  public void findAll_withCaseIdAndSupplierId_buildsRequest() throws IOException {
//  }
//
//  @Test
//  public void findAll_withCaseIdAndSupplierId_returnsAudits() throws IOException {
//
//  }

}