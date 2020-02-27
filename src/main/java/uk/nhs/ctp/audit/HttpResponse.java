package uk.nhs.ctp.audit;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import ca.uhn.fhir.rest.client.api.IHttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

public interface HttpResponse extends HttpExchange {


  int getStatus();

  String getStatusText();

  static HttpResponse from(IHttpResponse response) throws IOException {
    try (InputStream input = response.readEntity()) {

      byte[] body = input != null ? input.readAllBytes() : null;

      return new HttpResponse() {
        @Override
        public int getStatus() {
          return response.getStatus();
        }

        @Override
        public String getStatusText() {
          return response.getStatusInfo();
        }

        @Override
        public Map<String, List<String>> getHeaders() {
          return response.getAllHeaders();
        }

        @Override
        public byte[] getBody() {
          return body;
        }
      };
    }
  }

  static HttpResponse from(ClientHttpResponse response) throws IOException {

    try (InputStream input = response.getBody()) {
      byte[] body = input.readAllBytes();
      int status = response.getRawStatusCode();
      String statusText = response.getStatusText();

      return new HttpResponse() {
        @Override
        public int getStatus() {
          return status;
        }

        @Override
        public String getStatusText() {
          return statusText;
        }

        @Override
        public Map<String, List<String>> getHeaders() {
          return response.getHeaders();
        }

        @Override
        public byte[] getBody() {
          return body;
        }
      };
    }
  }

  static HttpResponse from(ContentCachingResponseWrapper response) {
    return new HttpResponse() {
      @Override
      public int getStatus() {
        return response.getStatus();
      }

      @Override
      public String getStatusText() {
        return null;
      }

      @Override
      public Map<String, ? extends Collection<String>> getHeaders() {
        return response.getHeaderNames().stream()
            .collect(toMap(
                identity(),
                response::getHeaders)
            );
      }

      @Override
      public byte[] getBody() {
        return response.getContentAsByteArray();
      }
    };
  }
}
