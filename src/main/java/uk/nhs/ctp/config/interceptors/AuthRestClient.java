package uk.nhs.ctp.config.interceptors;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthRestClient implements ClientHttpRequestInterceptor {

  @Value("${blob.server}")
  private String blobServer;

  @Value("${blob.server.auth.token}")
  private String blobServerAuthToken;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    if (request.getURI().toString().startsWith(blobServer)) {
      request.getHeaders().set(HttpHeaders.AUTHORIZATION, blobServerAuthToken);
    }
    return execution.execute(request, body);
  }
}
