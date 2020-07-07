package uk.nhs.ctp.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.utils.ZipBuilderFactory;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private final ZipBuilderFactory zipBuilderFactory;

  private static final String CASE_ID = "caseId";

  public byte[] zipAudits(List<AuditSession> audits, OperationType operationType) throws IOException {
    switch (operationType) {
      case ENCOUNTER:
        if (audits.stream().anyMatch(a -> !a.getAdditionalProperties().containsKey(CASE_ID))) {
          throw new UnsupportedOperationException("Encounter audits must have caseId set");
        }
        break;
      case SERVICE_SEARCH:
        if (audits.size() != 1 || audits.get(0).getEntries().size() != 1) {
          throw new UnsupportedOperationException(
              "Can only zip service_search audits one at a time");
        }
        break;
      default:
        throw new UnsupportedOperationException("Non-standard audit operation types not supported");
    }


    // decide which requests and responses to zip
    var zippableAudits = new ArrayList<HttpMessageAudit>();

    for (var audit : audits) {
      var baseName = operationType == OperationType.SERVICE_SEARCH
          ? operationType.getName()
          : "encounter" + audit.getAdditionalProperties().get(CASE_ID);

      if (isMethod(audit.getRequestMethod(), POST)) {
        zippableAudits.add(HttpMessageAudit.fromRequest(audit, baseName));
      }
      if (isMethod(audit.getRequestMethod(), GET, POST)) {
        zippableAudits.add(HttpMessageAudit.fromResponse(audit, baseName));
      }

      for (var entry : audit.getEntries()) {
        if (isMethod(entry.getRequestMethod(), POST)) {
          zippableAudits.add(HttpMessageAudit.fromRequest(entry, baseName));
        }
        if (isMethod(entry.getRequestMethod(), GET, POST)) {
          zippableAudits.add(HttpMessageAudit.fromResponse(entry, baseName));
        }
      }
    }

    // add chosen requests and responses to zip

    var zipBuilder = zipBuilderFactory.create();
    var sequenceCounter = new HashMap<String, Integer>();

    zippableAudits.sort(Comparator.comparing(HttpMessageAudit::getMoment));
    for (var messageAudit : zippableAudits) {

        var count = sequenceCounter.compute(
            messageAudit.getFilePath(),
            (path, existingCount) -> existingCount == null ? 1 : existingCount + 1);

        // TODO get content type and add extension to path
        var extension = naiveIsJson(messageAudit.getBody()) ? "json" : "xml";

        var fullPath = String.format(
            "%s.%d.%s.%s",
            messageAudit.filePath,
            count,
            messageAudit.getType().name().toLowerCase(),
            extension);

        zipBuilder.addEntry(fullPath, messageAudit.getBody(), messageAudit.getMoment());
    }

    return zipBuilder.buildAndCloseZip();
  }

  @Value
  @Builder
  private static class HttpMessageAudit {
    String filePath;
    String body;
    Instant moment;
    HttpMessageType type;

    private enum HttpMessageType { REQUEST, RESPONSE }

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

  private static boolean naiveIsJson(String text) {
    return text.length() > 0 && text.charAt(0) == '{';
  }

  private static boolean isMethod(String method, RequestMethod... expectedMethods) {
    return Stream.of(expectedMethods).map(Enum::name).anyMatch(method::equalsIgnoreCase);
  }

}
