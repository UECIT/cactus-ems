package uk.nhs.ctp.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RESTConfig {

  private final List<ClientHttpRequestInterceptor> clientInterceptors;

  @Bean
  public RestTemplateCustomizer restTemplateCustomizer() {
    return restTemplate -> {
      restTemplate.setRequestFactory(
          new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

      for (ClientHttpRequestInterceptor interceptor : clientInterceptors) {
        restTemplate.getInterceptors().add(interceptor);
      }
    };
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public RestTemplate auditRestTemplate() {
    // Currently only used for local-only services (i.e. audit server)
    // a timeout of 50 should be acceptable locally
    var timeout = 50;
    return new RestTemplateBuilder()
            .setConnectTimeout(timeout)
            .setReadTimeout(timeout)
            .build();
  }

  @Bean
  public RestTemplate blobRestTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

}
