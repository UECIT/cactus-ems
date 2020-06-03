package uk.nhs.ctp.config.interceptors;

import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.security.CactusToken;

@Component
public class AuthRestClient implements ClientHttpRequestInterceptor {

  @Value("${blob.server}")
  private String blobServer;

  @Value("${blob.server.auth.token}")
  private String blobServerAuthToken;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    // TODO match base URL to configurable authentication per service
    Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(auth -> (CactusToken) auth.getCredentials())
        .map(credentials -> "Bearer " + credentials.getToken())
        .ifPresent(token -> request.getHeaders().set(HttpHeaders.AUTHORIZATION, token));

    return execution.execute(request, body);
  }
}
