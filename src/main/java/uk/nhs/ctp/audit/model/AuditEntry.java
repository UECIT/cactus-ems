package uk.nhs.ctp.audit.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Represents calls made by this web service's FHIR client during an audit session
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuditEntry {

  String requestUrl;
  String requestMethod;
  String requestHeaders;
  String requestBody;

  String responseStatus;
  String responseHeaders;
  String responseBody;

  Instant dateOfEntry;

}
