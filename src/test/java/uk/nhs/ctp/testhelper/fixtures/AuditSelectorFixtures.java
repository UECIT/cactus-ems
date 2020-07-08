package uk.nhs.ctp.testhelper.fixtures;

import static java.lang.String.format;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditSelectorFixtures {

  /*
    get <- get, post, delete
    post <- get, post, options
    put <- get, post, patch
  */
  public static List<AuditSession> mixedMethodAudits(Instant createdAt) {
    return List.of(
        buildMethodAuditSession(createdAt, "get", "get", "post", "delete"),
        buildMethodAuditSession(createdAt, "post", "get", "post", "options"),
        buildMethodAuditSession(createdAt, "put", "get", "post", "patch"));
  }

  private static AuditSession buildMethodAuditSession(Instant createdAt, String method, String... innerMethods) {
    var entries = Stream.of(innerMethods)
        .map(innerMethod -> buildMethodAuditEntry(createdAt, innerMethod, method))
        .collect(Collectors.toUnmodifiableList());
    return AuditSession.builder()
        .entries(entries)
        .requestMethod(method.toUpperCase())
        .createdDate(createdAt)
        .additionalProperty("caseId", "_" + method)
        .requestUrl("http://" + method)
        .requestBody(format("{ request %s }", method))
        .responseBody(format("{ response %s }", method))
        .build();
  }

  private static AuditEntry buildMethodAuditEntry(Instant createdAt, String method, String outerMethod) {
    return AuditEntry.builder()
        .requestMethod(method.toUpperCase())
        .dateOfEntry(createdAt)
        .requestUrl(format("http://%s/inside/%s", method, outerMethod))
        .requestBody(format("{ request %s inside %s }", method, outerMethod))
        .responseBody(format("{ response %s inside %s }", method, outerMethod))
        .build();
  }

}
