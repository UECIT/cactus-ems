package uk.nhs.ctp.auditFinder;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.security.PutRoleMappingRequest;
import org.elasticsearch.client.security.PutRoleMappingResponse;
import org.elasticsearch.client.security.PutRoleRequest;
import org.elasticsearch.client.security.PutRoleResponse;
import org.elasticsearch.client.security.RefreshPolicy;
import org.elasticsearch.client.security.support.expressiondsl.fields.FieldRoleMapperExpression;
import org.elasticsearch.client.security.user.privileges.IndicesPrivileges;
import org.elasticsearch.client.security.user.privileges.Role;
import org.elasticsearch.client.security.user.privileges.Role.IndexPrivilegeName;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!dev")
public class ElasticSearchClient {

  private static final String ES_SERVICE_NAME = "es";
  private static final Pattern AWS_ES_PATTERN =
      Pattern.compile("https://[a-z0-9-]+\\.([a-z0-9-]+)\\.es\\.amazonaws\\.com");

  private final RestHighLevelClient baseClient;

  public ElasticSearchClient(@Value("${es.audit}") String endpoint) {
    Preconditions.checkState(
        StringUtils.isNotEmpty(endpoint),
        "Expected non-empty endpoint for ElasticSearch client");

    var baseClientBuilder = RestClient.builder(HttpHost.create(endpoint));

    var awsEndpointMatcher = AWS_ES_PATTERN.matcher(endpoint);
    if (awsEndpointMatcher.matches()) {
      log.info("Creating an ElasticSearchClient for an AWS endpoint");

      var signer = new AWS4Signer();
      signer.setServiceName(ES_SERVICE_NAME);
      signer.setRegionName(awsEndpointMatcher.group(1));

      var interceptor = new AWSRequestSigningApacheInterceptor(
          ES_SERVICE_NAME,
          signer,
          new DefaultAWSCredentialsProviderChain());
      baseClientBuilder.setHttpClientConfigCallback(clientConfig ->
          clientConfig.addInterceptorLast(interceptor));
    } else {
      log.info("Creating an ElasticSearchClient for a non-AWS endpoint");
    }

    this.baseClient = new RestHighLevelClient(baseClientBuilder);
  }

  public List<SearchHit> search(String index, SearchSourceBuilder source) throws IOException {
    var request = new SearchRequest()
        .indices(index)
        .source(source);

    log.info("Sending ElasticSearch request to index " + index + ":");
    log.info(request.toString());

    var response = baseClient.search(request, RequestOptions.DEFAULT);
    return Arrays.asList(response.getHits().getHits());
  }

  public void mapRole(String supplierId, String username) throws IOException {
    String roleName = supplierId + "_role";
    Role supplierRole = Role.builder()
        .name(roleName)
        .indicesPrivileges(IndicesPrivileges.builder()
            .privileges(IndexPrivilegeName.READ)
            .indices(supplierId + "-*")
            .build())
        .build();
    PutRoleRequest putRoleRequest = new PutRoleRequest(supplierRole, RefreshPolicy.NONE);
    PutRoleResponse putRoleResponse = baseClient.security()
        .putRole(putRoleRequest, RequestOptions.DEFAULT);

    if (!putRoleResponse.isCreated()) {
      log.error("Could not create role for supplier");
      return;
    }

    PutRoleMappingRequest mappingRequest = new PutRoleMappingRequest(
        roleName + "_mapping",
        true,
        Collections.singletonList(roleName),
        Collections.emptyList(),
        FieldRoleMapperExpression.ofUsername(username),
        null,
        RefreshPolicy.NONE
    );

    PutRoleMappingResponse putRoleMappingResponse = baseClient.security()
        .putRoleMapping(mappingRequest, RequestOptions.DEFAULT);

    if (!putRoleMappingResponse.isCreated()) {
      log.error("Could not create role mapping for supplier");
    }
  }
}
