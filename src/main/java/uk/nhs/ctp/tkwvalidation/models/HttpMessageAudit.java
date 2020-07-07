package uk.nhs.ctp.tkwvalidation.models;

import java.net.URI;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;

@Value
@Builder
public class HttpMessageAudit {
  String filePath;
  String body;
  Instant moment;
  HttpMessageType type;

  public static HttpMessageAudit fromRequest(AuditSession session, String basePath) {
    return HttpMessageAudit.builder()
        .filePath(mergePaths(basePath, session.getRequestUrl()))
        .body(session.getRequestBody())
        .moment(session.getCreatedDate())
        .type(HttpMessageType.REQUEST)
        .build();
  }

  public static HttpMessageAudit fromResponse(AuditSession session, String basePath) {
    return HttpMessageAudit.builder()
        .filePath(mergePaths(basePath, session.getRequestUrl()))
        .body(session.getResponseBody())
        .moment(session.getCreatedDate())
        .type(HttpMessageType.RESPONSE)
        .build();
  }

  public static HttpMessageAudit fromRequest(AuditEntry entry, String basePath) {
    return HttpMessageAudit.builder()
        .filePath(mergePaths(basePath, entry.getRequestUrl()))
        .body(entry.getRequestBody())
        .moment(entry.getDateOfEntry())
        .type(HttpMessageType.REQUEST)
        .build();
  }

  public static HttpMessageAudit fromResponse(AuditEntry entry, String basePath) {
    return HttpMessageAudit.builder()
        .filePath(mergePaths(basePath, entry.getRequestUrl()))
        .body(entry.getResponseBody())
        .moment(entry.getDateOfEntry())
        .type(HttpMessageType.RESPONSE)
        .build();
  }

  private static String mergePaths(String base, String path) {
    var uri = URI.create(path);
    var uriPath = uri.getHost() + uri.getPath();

    return String.format("%s/%s", base, uriPath);
  }
}
