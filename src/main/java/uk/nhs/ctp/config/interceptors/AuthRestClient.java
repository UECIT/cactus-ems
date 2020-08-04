package uk.nhs.ctp.config.interceptors;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.security.SupplierTokenResolver;

@Component
@RequiredArgsConstructor
public class AuthRestClient implements ClientHttpRequestInterceptor {

  private final SupplierTokenResolver tokenResolver;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    tokenResolver.resolve(request.getURI().toString())
        .map(token -> "Bearer " + token)
        .ifPresent(token -> request.getHeaders().set(HttpHeaders.AUTHORIZATION, token));

    return execution.execute(request, body);
  }
}
