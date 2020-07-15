package uk.nhs.ctp.tkwvalidation.model;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.http.MediaType.parseMediaType;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.nhs.ctp.audit.AuditParser;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;

@Value
@Builder
public class FhirMessageAudit {
  private static final List<MediaType> FHIR_FORMATS = Arrays.asList(
      parseMediaType("application/fhir+json"),
      parseMediaType("application/fhir+xml"),
      parseMediaType("application/json+fhir"),
      parseMediaType("application/xml+fhir")
  );

  String filePath;
  String requestBody;
  String responseBody;
  Instant moment;

  public static FhirMessageAudit from(AuditSession session, String basePath, boolean includeRequestBody) {
    var requestBody = includeRequestBody && isFhirContent(session.getRequestHeaders())
        ? session.getRequestBody()
        : null;
    var responseBody = isFhirContent(session.getResponseHeaders())
        ? session.getResponseBody()
        : null;

    return FhirMessageAudit.builder()
        .filePath(mergePaths(basePath, session.getRequestUrl()))
        .requestBody(requestBody)
        .responseBody(responseBody)
        .moment(session.getCreatedDate())
        .build();
  }

  public static FhirMessageAudit from(AuditEntry entry, String basePath, boolean includeRequestBody) {
    var requestBody = includeRequestBody && isFhirContent(entry.getRequestHeaders())
        ? entry.getRequestBody()
        : null;
    var responseBody = isFhirContent(entry.getResponseHeaders())
        ? entry.getResponseBody()
        : null;

    return FhirMessageAudit.builder()
        .filePath(mergePaths(basePath, entry.getRequestUrl()))
        .requestBody(requestBody)
        .responseBody(responseBody)
        .moment(entry.getDateOfEntry())
        .build();
  }

  private static String mergePaths(String base, String path) {
    var uri = URI.create(path);
    var uriPath = uri.getHost() + uri.getPath();

    return String.format("%s/%s", base, uriPath);
  }

  private static boolean isFhirContent(String headersString) {
    final var CONTENT_TYPE_HEADER = HttpHeaders.CONTENT_TYPE.toLowerCase();
    var allHeaders = AuditParser.getHeadersFrom(defaultIfEmpty(headersString, ""));
    if (!allHeaders.containsKey(CONTENT_TYPE_HEADER)) {
      return false;
    }

    var contentTypeHeaders = allHeaders.get(CONTENT_TYPE_HEADER);
    if (contentTypeHeaders.isEmpty()) {
      return false;
    }

    var contentType = parseMediaType(contentTypeHeaders.stream().findFirst().orElseThrow());

    return FHIR_FORMATS.stream().anyMatch(contentType::isCompatibleWith);
  }
}
