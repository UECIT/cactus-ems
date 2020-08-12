package uk.nhs.ctp.tkwvalidation;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.http.MediaType.parseMediaType;

import com.google.common.base.Strings;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

    String fullUrl = buildSessionURL(session);
    return FhirMessageAudit.builder()
        .filePath(mergePaths(basePath, fullUrl))
        .fullUrl(fullUrl)
        .requestBody(requestBody)
        .responseBody(responseBody)
        .moment(session.getCreatedDate())
        .build();
  }

  private String buildSessionURL(AuditSession session) {
    final var HOST_HEADER = HttpHeaders.HOST.toLowerCase();
    var allHeaders =
        auditParser.getHeadersFrom(defaultIfEmpty(session.getRequestHeaders(), ""));

    URI requestUrl = URI.create(session.getRequestUrl());
    return String.format("%s://%s%s",
        defaultString(singleHeader(allHeaders, "x-forwarded-proto"),
            requestUrl.getScheme(), "http"),
        defaultString(singleHeader(allHeaders, "host"),
            requestUrl.getHost(), "unknown-host"),
        requestUrl.getPath());
  }

  private String singleHeader(Map<String, Collection<String>> headers, String name) {
    return headers.getOrDefault(name, Collections.emptyList())
        .stream().findFirst()
        .orElse(null);
  }

  private String defaultString(String... strings) {
    for (String s : strings) {
      if (!Strings.isNullOrEmpty(s)) {
        return s;
      }
    }
    return null;
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
    StringBuilder sb = new StringBuilder(base);
    if (uri.isAbsolute()) {
      sb.append("/").append(uri.getHost());
    }
    sb.append(uri.getPath());

    return sb.toString();
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
