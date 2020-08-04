package uk.nhs.ctp.tkwvalidation;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

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

    var requestBuilder = RequestEntity.post(validatorUrl)
        .header(HttpHeaders.ACCEPT, "application/fhir+json")
        .header(HttpHeaders.CONTENT_TYPE, "application/zip")
        .header("Content-Transfer-Encoding", "base64");

    zipMetadata.toHeaders().toSingleValueMap()
        .forEach(requestBuilder::header);

    var request = requestBuilder.body(base64Zip);

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
