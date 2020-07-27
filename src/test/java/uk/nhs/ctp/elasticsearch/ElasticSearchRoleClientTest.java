package uk.nhs.ctp.elasticsearch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.cactus.common.elasticsearch.ElasticRestClientFactory;
import uk.nhs.ctp.elasticsearch.role.PutRoleMappingRequest;
import uk.nhs.ctp.elasticsearch.role.PutRoleRequest;
import uk.nhs.ctp.elasticsearch.role.PutRoleRequest.IndexPermissions;
import uk.nhs.ctp.testhelper.matchers.FunctionMatcher;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchRoleClientTest {

  private ElasticSearchRoleClient esRoleClient;

  @Mock
  private ElasticRestClientFactory mockFactory;

  private static final String MOCK_ENDPOINT = "mock.endpoint";

  @Before
  public void setup() {
    esRoleClient = new ElasticSearchRoleClient(mockFactory, new ObjectMapper());
    ReflectionTestUtils.setField(esRoleClient, "endpoint", MOCK_ENDPOINT);
  }

  @Test
  public void shouldCreateAndMapRole() throws Exception {
    CloseableHttpClient mockClient = mock(CloseableHttpClient.class);
    when(mockFactory.httpClient(MOCK_ENDPOINT))
        .thenReturn(mockClient);

    String roleName = "test_role";
    PutRoleRequest putRoleRequest = PutRoleRequest.builder()
        .indexPermission(IndexPermissions.builder()
            .indexPattern("test-role-index")
            .allowedAction("some action")
            .build())
        .build();

    PutRoleMappingRequest putRoleMappingRequest = PutRoleMappingRequest.builder()
        .user("test_user")
        .build();

    esRoleClient.mapRole(roleName, putRoleRequest, putRoleMappingRequest);

    var putCaptor = ArgumentCaptor.forClass(HttpPut.class);
    HttpHost expectedHost = HttpHost.create(MOCK_ENDPOINT);
    verify(mockClient, times(2))
        .execute(eq(expectedHost), putCaptor.capture(), any(BasicResponseHandler.class));

    List<HttpPut> putRequests = putCaptor.getAllValues();
    List<Header> expectedHeaders = Collections
        .singletonList(new BasicHeader("Content-Type", "application/json"));
    assertThat(putRequests, contains(
        isPut(
            "/_opendistro/_security/api/roles/test_role",
            expectedHeaders,
            putRoleRequest),
        isPut(
            "/_opendistro/_security/api/rolesmapping/test_role",
            expectedHeaders,
            putRoleMappingRequest)
    ));
  }

  private static FunctionMatcher<HttpPut> isPut(String uri, List<Header> headers, Object entity) {
    return new FunctionMatcher<>(putRequest -> {
      try {
        return Objects.equals(putRequest.getURI(), URI.create(uri))
            && headers.stream()
                .allMatch(header -> Arrays.stream(putRequest.getAllHeaders())
                  .anyMatch(putHeader ->
                      putHeader.getName().equals(header.getName())
                      && putHeader.getValue().equals(header.getValue())))
            && Arrays.equals(
                putRequest.getEntity().getContent().readAllBytes(),
            new ObjectMapper().writeValueAsBytes(entity));
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }, "{URI:" + uri + ", Headers:" + headers + ", Entity:" + entity + "}");
  }

}