package uk.nhs.ctp.auditFinder;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.auditFinder.role.PutRoleMappingRequest;
import uk.nhs.ctp.auditFinder.role.PutRoleRequest;

@Component
@Slf4j
@Profile("!dev")
@RequiredArgsConstructor
public class ElasticSearchClient {

  @Value("${es.audit}")
  private String endpoint;

  private final ElasticRestClientFactory clientFactory;
  private final ObjectMapper objectMapper;

  private static final String PUT_ROLE = "/_opendistro/_security/api/roles/";
  private static final String PUT_ROLE_MAPPING = "/_opendistro/_security/api/rolesmapping/";

  public List<SearchHit> search(String index, SearchSourceBuilder source) throws IOException {
    var request = new SearchRequest()
        .indices(index)
        .source(source);

    log.info("Sending ElasticSearch request to index " + index + ":");
    log.info(request.toString());

    var response = clientFactory.highLevelClient(endpoint)
        .search(request, RequestOptions.DEFAULT);
    return Arrays.asList(response.getHits().getHits());
  }

  public void mapRole(
      String roleName,
      PutRoleRequest roleRequest,
      PutRoleMappingRequest roleMappingRequest) throws IOException {
    CloseableHttpClient client = clientFactory.httpClient(endpoint);
    HttpHost host = HttpHost.create(endpoint);
    Header contentType = new BasicHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());

    HttpPut putRoleRequest = new HttpPut(PUT_ROLE + roleName);
    putRoleRequest.setEntity(createEntity(roleRequest));
    putRoleRequest.setHeader(contentType);
    client.execute(host, putRoleRequest, new BasicResponseHandler());

    HttpPut putRoleMappingRequest = new HttpPut(PUT_ROLE_MAPPING + roleName);
    putRoleMappingRequest.setEntity(createEntity(roleMappingRequest));
    putRoleMappingRequest.setHeader(contentType);
    client.execute(host, putRoleMappingRequest, new BasicResponseHandler());
  }

  private HttpEntity createEntity(Object entity) throws IOException {
    return new ByteArrayEntity(objectMapper.writeValueAsBytes(entity));
  }
}
