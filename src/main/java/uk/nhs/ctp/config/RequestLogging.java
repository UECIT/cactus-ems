package uk.nhs.ctp.config;

import static java.util.Arrays.asList;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Configuration
@Profile("log_rest")
public class RequestLogging {

  @Bean
  public RestTemplateCustomizer restTemplateCustomizer(Interceptor interceptor) {
    return restTemplate -> restTemplate.getInterceptors().add(interceptor);
  }

  @Component
  public static class Interceptor implements ClientHttpRequestInterceptor {

    private static List<MediaType> textContentTypes = asList(
        MediaType.valueOf("text/*"),
        MediaType.valueOf("application/*+xml")
    );

    private static List<MediaType> jsonContentTypes = asList(
        MediaType.valueOf("application/*+json"),
        MediaType.APPLICATION_JSON
    );

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper;

    public Interceptor(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {

      logRequest(request, body);
      ClientHttpResponse response = new ResponseWrapper(execution.execute(request, body));
      logResponse(request, response);
      return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
      log.info("============= request begin ({}) =============", request.hashCode());
      log.info("URI         : {}", request.getURI());
      log.info("Method      : {}", request.getMethod());
      log.info("Headers     : {}", request.getHeaders());
      MediaType contentType = request.getHeaders().getContentType();
      formatBody(new ByteArrayInputStream(body), contentType);
      log.info("============= request end ({}) ===============", request.hashCode());
    }

    private void logResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
      log.info("=========== response begin ({}) ==============", request.hashCode());
      log.info("URI          : {}", request.getURI());
      log.info("Method       : {}", request.getMethod());
      log.info("Status code  : {}", response.getStatusCode());
      log.info("Status text  : {}", response.getStatusText());
      log.info("Headers      : {}", response.getHeaders());

      MediaType contentType = response.getHeaders().getContentType();
      formatBody(response.getBody(), contentType);
      log.info("=========== response end ({}) ================", request.hashCode());
    }

    private void formatBody(InputStream body, MediaType contentType) throws IOException {
      if (jsonContentTypes.stream().anyMatch(contentType::isCompatibleWith)) {
        String responseBody = StreamUtils.copyToString(body, contentType.getCharset());
        log.info("Response body: {}", responseBody);
      } else if (textContentTypes.stream().anyMatch(contentType::isCompatibleWith)) {
        String responseBody = StreamUtils.copyToString(body, contentType.getCharset());
        log.info("Response body: {}", responseBody);
      } else {
        log.info("Request body: NON TEXT: {}", contentType);
      }
    }

  }

  /**
   * Based on org.springframework.http.client.BufferingClientHttpResponseWrapper
   */
  final static class ResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private byte[] body;

    ResponseWrapper(ClientHttpResponse response) {
      this.response = response;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
      return this.response.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
      return this.response.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
      return this.response.getStatusText();
    }

    @Override
    public HttpHeaders getHeaders() {
      return this.response.getHeaders();
    }

    @Override
    public InputStream getBody() throws IOException {
      if (this.body == null) {
        this.body = StreamUtils.copyToByteArray(this.response.getBody());
      }
      return new ByteArrayInputStream(this.body);
    }

    @Override
    public void close() {
      this.response.close();
    }

  }
}
