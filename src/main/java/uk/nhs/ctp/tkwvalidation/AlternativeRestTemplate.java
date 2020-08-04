package uk.nhs.ctp.tkwvalidation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

// Designed as a very restricted, similarly-interfaced alternative to RestTemplate
// Required as RestTemplate mangles Content-Transfer-Encoding header
@Component
public class AlternativeRestTemplate {

  public ResponseEntity<String> exchange(RequestEntity<byte[]> request) throws IOException {
    try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

      var requestBuilder = RequestBuilder.create(request.getMethod().name())
          .setUri(request.getUrl())
          .setEntity(new ByteArrayEntity(request.getBody()));

      for (var headerName : request.getHeaders().keySet()) {
        for (var headerValue : request.getHeaders().getValuesAsList(headerName)) {
          requestBuilder.addHeader(headerName, headerValue);
        }
      }

      try (CloseableHttpResponse response = httpClient.execute(requestBuilder.build())) {

        var responseBuilder = ResponseEntity.status(response.getStatusLine().getStatusCode());

        for (var header : response.getAllHeaders()) {
          for (var headerElement : header.getElements()) {
            requestBuilder.addHeader(headerElement.getName(), headerElement.getValue());
          }
        }

        String content = IOUtils.toString(response.getEntity().getContent(), UTF_8);
        return responseBuilder.body(content);
      }
    }
  }

}
