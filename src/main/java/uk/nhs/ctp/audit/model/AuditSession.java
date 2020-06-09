package uk.nhs.ctp.audit.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

/**
 * Represents a call to this server containing calls to other servers
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class AuditSession {

  String requestOrigin;
  String requestUrl;
  String requestMethod;
  String requestHeaders;
  String requestBody;

  String responseStatus;
  String responseHeaders;
  String responseBody;

  Instant createdDate;
  @Singular
  List<AuditEntry> entries;
  @Singular
  Map<String, String> additionalProperties;

}
