package uk.nhs.ctp.tkwvalidation;

import java.io.IOException;
import java.net.URI;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata.Headers;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditDispatcher {
  private final AlternativeRestTemplate restTemplate;

  @Value("${reports.validation.server}")
  private String reportValidationServer;

  public String getValidationUrl() {
    return reportValidationServer + "/$evaluate";
  }

  public void dispatchToTkw(byte[] zipData, AuditMetadata zipMetadata) throws IOException {

    var base64Zip = Base64.getEncoder().encode(zipData);
    var validatorUrl = URI.create(getValidationUrl());
    var interactionDate = zipMetadata.getInteractionDate()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT);

    var request = RequestEntity.post(validatorUrl)
        .header(HttpHeaders.ACCEPT, "application/fhir+json")
        .header(HttpHeaders.CONTENT_TYPE, "application/zip")
        .header("Content-Transfer-Encoding", "base64")
        .header(Headers.SUPPLIER_ID, zipMetadata.getSupplierId())
        .header(Headers.API_VERSION, zipMetadata.getApiVersion().getVersion())
        .header(Headers.INTERACTION_TYPE, zipMetadata.getInteractionType().getName())
        // TODO CDSCT-400: enable the following line
//        .header(Headers.INTERACTION_ID, zipMetadata.getInteractionId())
        .header(Headers.INTERACTION_DATE, interactionDate)
        .header(Headers.SERVICE_ENDPOINT, zipMetadata.getServiceEndpoint())
        .body(base64Zip);

    log.info("Sending validation request to TKW: {}", request);

    var response = restTemplate.exchange(request);
    if (response.getStatusCode() != HttpStatus.ACCEPTED) {
      log.warn("Call to validation service on {} returned status {}, with message:\n{}",
          validatorUrl,
          response.getStatusCode(),
          response.getBody());
    }
  }
}
