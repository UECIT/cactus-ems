package uk.nhs.ctp.elasticsearch;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.elasticsearch.ElasticRestClientFactory;
import uk.nhs.ctp.elasticsearch.role.PutRoleMappingRequest;
import uk.nhs.ctp.elasticsearch.role.PutRoleRequest;

@Component
@Slf4j
@Profile("!dev")
@RequiredArgsConstructor
public class ElasticSearchRoleClient {

  @Value("${es.audit}")
  private String endpoint;

  private final ElasticRestClientFactory clientFactory;
  private final ObjectMapper objectMapper;

  private static final String PUT_ROLE = "/_opendistro/_security/api/roles/";
  private static final String PUT_ROLE_MAPPING = "/_opendistro/_security/api/rolesmapping/";

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
