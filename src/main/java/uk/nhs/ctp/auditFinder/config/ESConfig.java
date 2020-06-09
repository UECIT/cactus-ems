package uk.nhs.ctp.auditFinder.config;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.ctp.auditFinder.AWSRequestSigningApacheInterceptor;

@Configuration
public class ESConfig {

  private static final String ES_SERVICE_NAME = "es";
  private static final Pattern AWS_ES_PATTERN =
      Pattern.compile("https://[a-z0-9-]+\\.([a-z0-9-]+)\\.es\\.amazonaws\\.com");

  @Value("${es.audit}")
  private String endpoint;

  @Bean
  public RestHighLevelClient esClient() {
    // TODO CDSCT-164: require non-empty endpoint
    if (StringUtils.isEmpty(endpoint)) {
      return null;
    }

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

    return new RestHighLevelClient(baseClientBuilder);
  }

}
