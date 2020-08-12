package uk.nhs.ctp.tkwvalidation;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.http.MediaType.parseMediaType;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.audit.AuditParser;
import uk.nhs.cactus.common.audit.model.AuditEntry;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;

@Component
@RequiredArgsConstructor
public class FhirMessageAuditTransformer {

  private static final List<MediaType> FHIR_FORMATS = Arrays.asList(
      parseMediaType("application/fhir+json"),
      parseMediaType("application/fhir+xml"),
      parseMediaType("application/json+fhir"),
      parseMediaType("application/xml+fhir")
  );

  private final AuditParser auditParser;

  public FhirMessageAudit from(AuditSession session, String basePath, boolean includeRequestBody) {
    var requestBody = includeRequestBody && isFhirContent(session.getRequestHeaders())
        ? session.getRequestBody()
        : null;
    var responseBody = isFhirContent(session.getResponseHeaders())
        ? session.getResponseBody()
        : null;

    return FhirMessageAudit.builder()
        .filePath(mergePaths(basePath, session.getRequestUrl()))
        .fullUrl(fullUrl(session.getRequestHeaders(), session.getRequestUrl()))
        .requestBody(requestBody)
        .responseBody(responseBody)
        .moment(session.getCreatedDate())
        .build();
  }

  private String fullUrl(String requestHeaders, String requestUrl) {
    final var HOST_HEADER = HttpHeaders.HOST.toLowerCase();
    var allHeaders = auditParser.getHeadersFrom(defaultIfEmpty(requestHeaders, ""));
    if (!allHeaders.containsKey(HOST_HEADER)) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    sb.append(singleHeader(allHeaders, "x-forwarded-proto", "http"));
    sb.append("://").append(singleHeader(allHeaders, "host", "unknown-host"));
    sb.append(requestUrl);

    return sb.toString();
  }

  private String singleHeader(Map<String, Collection<String>> headers, String name,
      String defaultValue) {
    return headers.getOrDefault(name, List.of(defaultValue)).iterator().next();
  }

  public FhirMessageAudit from(AuditEntry entry, String basePath, boolean includeRequestBody) {
    var requestBody = includeRequestBody && isFhirContent(entry.getRequestHeaders())
        ? entry.getRequestBody()
        : null;
    var responseBody = isFhirContent(entry.getResponseHeaders())
        ? entry.getResponseBody()
        : null;

    return FhirMessageAudit.builder()
        .filePath(mergePaths(basePath, entry.getRequestUrl()))
        .fullUrl(entry.getRequestUrl())
        .requestBody(requestBody)
        .responseBody(responseBody)
        .moment(entry.getDateOfEntry())
        .build();
  }

  private String mergePaths(String base, String path) {
    var uri = URI.create(path);
    var uriPath = uri.getHost() + uri.getPath();

    return String.format("%s/%s", base, uriPath);
  }

  private boolean isFhirContent(String headersString) {
    final var CONTENT_TYPE_HEADER = HttpHeaders.CONTENT_TYPE.toLowerCase();
    var allHeaders = auditParser.getHeadersFrom(defaultIfEmpty(headersString, ""));
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
