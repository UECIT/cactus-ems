package uk.nhs.ctp.tkwvalidation;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.http.MediaType.parseMediaType;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.audit.AuditParser;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
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
        .requestBody(requestBody)
        .responseBody(responseBody)
        .moment(session.getCreatedDate())
        .build();
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
