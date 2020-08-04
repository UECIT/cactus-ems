package uk.nhs.ctp.config.interceptors;

import static java.util.Arrays.asList;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
@Profile("log_rest")
@Slf4j
public class LogRestClient implements ClientHttpRequestInterceptor {

  private static List<MediaType> textContentTypes = asList(
      MediaType.valueOf("text/*"),
      MediaType.valueOf("application/*+xml"),
      MediaType.valueOf("application/*+json"),
      MediaType.APPLICATION_JSON
  );

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    logRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    logResponse(request, response);
    return response;
  }

  private void logRequest(HttpRequest request, byte[] body) throws IOException {
    log.info("============= request begin ({}) =============", request.hashCode());
    log.info("URI         : {}", request.getURI());
    log.info("Method      : {}", request.getMethod());
    log.info("Headers     : {}", request.getHeaders());
    if (body.length > 0) {
      MediaType contentType = request.getHeaders().getContentType();
      formatBody(new ByteArrayInputStream(body), contentType);
    }
    log.info("============= request end ({}) ===============", request.hashCode());
  }

  private void logResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
    log.info("=========== response begin ({}) ==============", request.hashCode());
    log.info("URI          : {}", request.getURI());
    log.info("Method       : {}", request.getMethod());
    log.info("Status code  : {}", response.getStatusCode());
    log.info("Status text  : {}", response.getStatusText());
    log.info("Headers      : {}", response.getHeaders());

    String contentLengthHeader = response.getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH);
    if (response.getStatusCode().is2xxSuccessful() && (
        Strings.isNullOrEmpty(contentLengthHeader)
            || response.getHeaders().getContentLength() > 0)) {
      MediaType contentType = response.getHeaders().getContentType();
      formatBody(response.getBody(), contentType);
    }
    log.info("=========== response end ({}) ================", request.hashCode());
  }

  private void formatBody(InputStream body, MediaType contentType) throws IOException {
    if (textContentTypes.stream().anyMatch(contentType::isCompatibleWith)) {
      String bodyString = StreamUtils.copyToString(body,
          ObjectUtils.defaultIfNull(contentType.getCharset(), Charsets.UTF_8));
      log.info("Body: {}", bodyString);
    } else {
      log.info("Body: NON TEXT: {}", contentType);
    }
  }

}
