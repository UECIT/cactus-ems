package uk.nhs.ctp.auditFinder.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.ctp.auditFinder.ElasticSearchClient;

@Configuration
public class ESConfig {

  @Value("${es.audit}")
  private String endpoint;

  @Bean
  public ElasticSearchClient esClient() {
    // TODO CDSCT-164: require non-empty endpoint
    if (StringUtils.isEmpty(endpoint)) {
      return null;
    }

    return ElasticSearchClient.forEndpoint(endpoint);
  }

}
