package uk.nhs.ctp.auditFinder;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ElasticRestClientFactory {

  private static final String ES_SERVICE_NAME = "es";
  private static final Pattern AWS_ES_PATTERN =
      Pattern.compile("https://[a-z0-9-]+\\.([a-z0-9-]+)\\.es\\.amazonaws\\.com(:\\d+)?");

  public RestHighLevelClient highLevelClient(String endpoint) {
    Preconditions.checkState(
        StringUtils.isNotEmpty(endpoint),
        "Expected non-empty endpoint for ElasticSearch client");

    var baseClientBuilder = RestClient.builder(HttpHost.create(endpoint));

    interceptor(endpoint)
        .ifPresentOrElse(interceptor -> baseClientBuilder.setHttpClientConfigCallback(clientConfig ->
            clientConfig.addInterceptorLast(interceptor)),
            () -> log.info("Creating an ElasticSearchClient for a non-AWS endpoint"));

    return new RestHighLevelClient(baseClientBuilder);
  }

  public CloseableHttpClient httpClient(String endpoint) {
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    interceptor(endpoint)
        .map(httpClientBuilder::addInterceptorFirst);

    return httpClientBuilder.build();
  }

  private Optional<AWSRequestSigningApacheInterceptor> interceptor(String endpoint) {
    var awsEndpointMatcher = AWS_ES_PATTERN.matcher(endpoint);
    if (awsEndpointMatcher.matches()) {
      log.info("Creating an ElasticSearchClient for an AWS endpoint");

      var signer = new AWS4Signer();
      signer.setServiceName(ES_SERVICE_NAME);
      signer.setRegionName(awsEndpointMatcher.group(1));

      return Optional.of(new AWSRequestSigningApacheInterceptor(
          ES_SERVICE_NAME,
          signer,
          new DefaultAWSCredentialsProviderChain()));
    }
    return Optional.empty();
  }

}
