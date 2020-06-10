package uk.nhs.ctp.auditFinder;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticSearchClient {

  private static final String ES_SERVICE_NAME = "es";
  private static final Pattern AWS_ES_PATTERN =
      Pattern.compile("https://[a-z0-9-]+\\.([a-z0-9-]+)\\.es\\.amazonaws\\.com");

  private final RestHighLevelClient baseClient;

  public static ElasticSearchClient forEndpoint(String endpoint) {
    Preconditions.checkState(
        StringUtils.isNotEmpty(endpoint),
        "Expected non-empty endpoint for ElasticSearch client");

    var baseClientBuilder = RestClient.builder(HttpHost.create(endpoint));

    var awsEndpointMatcher = AWS_ES_PATTERN.matcher(endpoint);
    if (awsEndpointMatcher.matches()) {
      var signer = new AWS4Signer();
      signer.setServiceName(ES_SERVICE_NAME);
      signer.setRegionName(awsEndpointMatcher.group(1));

      var interceptor = new AWSRequestSigningApacheInterceptor(
          ES_SERVICE_NAME,
          signer,
          new DefaultAWSCredentialsProviderChain());
      baseClientBuilder.setHttpClientConfigCallback(clientConfig ->
          clientConfig.addInterceptorLast(interceptor));
    }

    return new ElasticSearchClient(new RestHighLevelClient(baseClientBuilder));
  }

  public List<SearchHit> search(String index, SearchSourceBuilder source) throws IOException {
    var request = new SearchRequest()
        .indices(index)
        .source(source);

    var response = baseClient.search(request, RequestOptions.DEFAULT);
    return Arrays.asList(response.getHits().getHits());
  }
}
