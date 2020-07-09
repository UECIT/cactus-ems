package uk.nhs.ctp.tkwvalidation;

import ca.uhn.fhir.context.FhirContext;
import java.net.URI;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditDispatcher {
  private final FhirContext fhirContext;
  private final RestTemplate restTemplate;

  @Value("${reports.validation.server}")
  private String reportValidationServer;

  public String getValidationUrl() {
    return reportValidationServer + "/$evaluate";
  }

  public String dispatchToTkw(byte[] zipData) {
    var base64Zip = Base64.getEncoder().encode(zipData);
    var validatorUrl = URI.create(getValidationUrl());

    var request = RequestEntity.post(validatorUrl)
        .header(HttpHeaders.ACCEPT, "application/fhir+json")
        .header(HttpHeaders.CONTENT_TYPE, "application/zip")
        .header("Content-Transfer-Encoding", "base64")
        .body(base64Zip);

    var response = restTemplate.exchange(request, String.class);
    if (response.getStatusCode() != HttpStatus.ACCEPTED) {
      log.warn("Call to validation service on {} returned status {}, with message:\n{}",
          validatorUrl,
          response.getStatusCode(),
          response.getBody());
    }

      var operationOutcome = fhirContext.newJsonParser()
            .parseResource(OperationOutcome.class, response.getBody());
      String html = operationOutcome.getIssueFirstRep().getDiagnostics();

      return cleanHtml(html);
  }

  private String cleanHtml(String html) {
    Whitelist whitelist = Whitelist.relaxed()
        .addTags("hr")
        .addAttributes("tr", "bgcolor");

    return Jsoup.clean(html, whitelist);
  }
}
