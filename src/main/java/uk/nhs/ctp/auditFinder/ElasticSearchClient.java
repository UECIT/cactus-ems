package uk.nhs.ctp.auditFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.security.user.privileges.Role.IndexPrivilegeName;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.auditFinder.role.PutRoleMappingRequest;
import uk.nhs.ctp.auditFinder.role.PutRoleRequest;
import uk.nhs.ctp.auditFinder.role.PutRoleRequest.IndexPermissions;

@Component
@Slf4j
@Profile("!dev")
@RequiredArgsConstructor
public class ElasticSearchClient {

  @Value("${es.audit}")
  private String endpoint;

  private final ElasticRestClientFactory clientFactory;
  private final ObjectMapper objectMapper;

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

  public void mapRole(String supplierId, String username) throws IOException {
    CloseableHttpClient client = clientFactory.httpClient(endpoint);
    String roleName = supplierId + "_role";

    PutRoleRequest roleRequest = PutRoleRequest.builder()
        .indexPermission(IndexPermissions.builder()
            .allowedAction(IndexPrivilegeName.READ)
            .indexPattern(supplierId + "-*")
            .build())
        .build();

    URI uri = URI.create(endpoint);
    HttpHost host = new HttpHost(uri.getHost(), uri.getPort());
    HttpPut putRoleRequest = new HttpPut("_opendistro/_security/api/roles/" + roleName);
    byte[] putRoleRequestBytes = objectMapper.writeValueAsBytes(roleRequest);
    putRoleRequest.setEntity(new ByteArrayEntity(putRoleRequestBytes));
    putRoleRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    putRoleRequest.setHeader(HttpHeaders.HOST, uri.getHost());
    log.info("Put role request: {}", putRoleRequest);
    log.info("Put role request entity: {}", new String(putRoleRequestBytes));
    CloseableHttpResponse putRoleResponse = client.execute(host, putRoleRequest);
    log.info("Put role response: Status:{}, Message:{}, Entity:{}",
        putRoleResponse.getStatusLine().getStatusCode(),
        putRoleResponse.getStatusLine().getReasonPhrase(),
        new String(putRoleResponse.getEntity().getContent().readAllBytes()));

    PutRoleMappingRequest roleMappingRequest = PutRoleMappingRequest.builder()
        .user(username)
        .build();

    HttpPut putRoleMappingRequest = new HttpPut(" _opendistro/_security/api/rolesmapping/" + roleName);
    putRoleMappingRequest.setEntity(new ByteArrayEntity(objectMapper.writeValueAsBytes(roleMappingRequest)));
    putRoleMappingRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    putRoleMappingRequest.setHeader(HttpHeaders.HOST, uri.getHost());
    CloseableHttpResponse putMappingResponse = client.execute(host, putRoleMappingRequest);
    log.info("Put role response: Status:{}, Message:{}, Entity:{}",
        putMappingResponse.getStatusLine().getStatusCode(),
        putMappingResponse.getStatusLine().getReasonPhrase(),
        new String(putMappingResponse.getEntity().getContent().readAllBytes()));
  }
}
