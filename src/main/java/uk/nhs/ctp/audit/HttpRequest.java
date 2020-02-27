package uk.nhs.ctp.audit;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import ca.uhn.fhir.rest.client.api.IHttpRequest;
import com.google.common.collect.Streams;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.web.util.ContentCachingRequestWrapper;

public interface HttpRequest extends HttpExchange {

  String getMethod();

  String getUri();

  String getError();

  static HttpRequest from(IHttpRequest theRequest) {

    return new HttpRequest() {
      private String error;
      private byte[] body;

      {
        try {
          String requestBody = theRequest.getRequestBodyFromStream();
          if (requestBody != null) {
            body = requestBody.getBytes(StandardCharsets.UTF_8);
          }
        } catch (IOException e) {
          error = e.getMessage();
        }
      }

      @Override
      public String getMethod() {
        return theRequest.getHttpVerbName();
      }

      @Override
      public String getUri() {
        return theRequest.getUri();
      }

      @Override
      public String getError() {
        return error;
      }

      @Override
      public Map<String, List<String>> getHeaders() {
        return theRequest.getAllHeaders();
      }

      @Override
      public byte[] getBody() {
        return body;
      }
    };
  }

  static HttpRequest from(org.springframework.http.HttpRequest request, byte[] body) {
    return new HttpRequest() {

      @Override
      public String getMethod() {
        return request.getMethod().name();
      }

      @Override
      public String getUri() {
        return request.getURI().toString();
      }

      @Override
      public String getError() {
        return null;
      }

      @Override
      public Map<String, List<String>> getHeaders() {
        return request.getHeaders();
      }

      @Override
      public byte[] getBody() {
        return body;
      }
    };
  }

  static HttpRequest from(ContentCachingRequestWrapper request) {
    return new HttpRequest() {
      @Override
      public String getMethod() {
        return request.getMethod();
      }

      @Override
      public String getUri() {
        return request.getRequestURI();
      }

      @Override
      public String getError() {
        return null;
      }

      @Override
      public Map<String, ? extends List<String>> getHeaders() {
        return Streams
            .stream(request.getHeaderNames().asIterator())
            .collect(toMap(
                identity(),
                name -> newArrayList(request.getHeaders(name).asIterator()))
            );
      }

      @Override
      public byte[] getBody() {
        return request.getContentAsByteArray();
      }
    };
  }
}
