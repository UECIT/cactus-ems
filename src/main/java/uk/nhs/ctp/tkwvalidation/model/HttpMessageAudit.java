package uk.nhs.ctp.tkwvalidation.model;

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
  String requestBody;
  String responseBody;
  Instant moment;

  public static HttpMessageAudit from(AuditSession session, String basePath, boolean includeRequestBody) {
    return HttpMessageAudit.builder()
        .filePath(mergePaths(basePath, session.getRequestUrl()))
        .requestBody(includeRequestBody ? session.getRequestBody() : null)
        .responseBody(session.getResponseBody())
        .moment(session.getCreatedDate())
        .build();
  }

  public static HttpMessageAudit from(AuditEntry entry, String basePath, boolean includeRequestBody) {
    return HttpMessageAudit.builder()
        .filePath(mergePaths(basePath, entry.getRequestUrl()))
        .requestBody(includeRequestBody ? entry.getRequestBody() : null)
        .responseBody(entry.getResponseBody())
        .moment(entry.getDateOfEntry())
        .build();
  }

  private static String mergePaths(String base, String path) {
    var uri = URI.create(path);
    var uriPath = uri.getHost() + uri.getPath();

    return String.format("%s/%s", base, uriPath);
  }
}
