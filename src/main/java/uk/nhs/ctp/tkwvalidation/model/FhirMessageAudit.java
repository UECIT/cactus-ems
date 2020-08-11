package uk.nhs.ctp.tkwvalidation.model;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FhirMessageAudit {
  String filePath;
  String fullUrl;
  String requestBody;
  String responseBody;
  Instant moment;
}
