package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Reference;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.ctp.enums.ContentType;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.Reportable;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

  private final Collection<Reportable> reportServices;
  private final ValidationService validationService;
  private final ObjectMapper mapper;
  private final FhirContext fhirContext;

  @Value("${reports.server}")
  private String reportsServer;

  @Value("${reports.server.auth.token}")
  private String reportServerToken;

  @Value("${reports.validation.server}")
  private String reportValidationServer;

  public Collection<ReportsDTO> generateReports(
      ReportRequestDTO request) throws JAXBException, JsonProcessingException {

    Collection<ReportsDTO> reports = new ArrayList<>();

    for (Reportable service : reportServices) {
      try {
        reports.add(service.generate(request.clone()));
      } catch (Exception e) {
        log.error(MessageFormat.format("Error creating report {0} ",
            service.getClass().getSimpleName().replace("Service", "")), e);
      }

    }

    return reports;
  }

  public Collection<ReportsDTO> generateReports(String encounterRef) {
    ArrayList<ReportsDTO> reports = new ArrayList<>();

    transformEncounterReport(encounterRef, reports);
    reports.add(validate(encounterRef));

    return reports;
  }

  private void transformEncounterReport(String encounterRef, ArrayList<ReportsDTO> reports) {
    RestTemplate template = new RestTemplate();
    template.getInterceptors().add((request, body, execution) -> {
      if (request.getURI().toString().startsWith(reportsServer)) {
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, reportServerToken);
      }
      return execution.execute(request, body);
    });

    String reportsUrl = UriComponentsBuilder.fromHttpUrl(reportsServer)
        .queryParam("encounter", encounterRef)
        .toUriString();

    log.info("Sending an http post to: {}", reportsUrl);

    try {
      ResponseEntity<String> response = template
          .exchange(reportsUrl, HttpMethod.POST, null, String.class);

      if (response.getStatusCode() != HttpStatus.OK) {
        log.error("Creating Reports: Unexpected response status: " + response.getStatusCode());

        reports.add(ReportsDTO.builder()
            .contentType(ContentType.HTML)
            .reportType(ReportType.ECDS)
            .request(reportsUrl)
            .response("Creating Reports: Unexpected response status: " + response.getStatusCode())
            .build());
      } else {
        Map<String, String> parsedResponse = mapper.readValue(response.getBody(),
            new TypeReference<Map<String, String>>() {
            });

        if (parsedResponse.containsKey("ecds")) {
          reports.add(ReportsDTO.builder()
              .contentType(ContentType.XML)
              .reportType(ReportType.ECDS)
              .request(reportsUrl)
              .response(parsedResponse.get("ecds"))
              .build());
        }

        if (parsedResponse.containsKey("iucds")) {
          reports.add(ReportsDTO.builder()
              .contentType(ContentType.XML)
              .reportType(ReportType.IUCDS)
              .request(reportsUrl)
              .response(parsedResponse.get("iucds"))
              .build());
        }
      }
    } catch (IOException e) {
      reports.add(ReportsDTO.builder()
          .contentType(ContentType.HTML)
          .reportType(ReportType.ECDS)
          .request(reportsUrl)
          .response("Creating Reports: Unable to parse response: " + e.getMessage())
          .build());
    } catch (RestClientException e) {
      reports.add(ReportsDTO.builder()
          .contentType(ContentType.HTML)
          .reportType(ReportType.ECDS)
          .request(reportsUrl)
          .response("Creating Reports: Error contacting reports service: " + e.getMessage())
          .build());
    }
  }

  public ReportsDTO validate(String encounterRef) {
    try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
      byte[] zipData = validationService
          .zipResources(new Reference(encounterRef).getReferenceElement().getIdPartAsLong());

      var base64Zip = Base64.getEncoder().encode(zipData);

      // Send to validation service
      String validatorUrl = reportValidationServer + "/$evaluate";

      HttpUriRequest request = RequestBuilder
          .post(validatorUrl)
          .addHeader(HttpHeaders.ACCEPT, "application/fhir+json")
          .addHeader(HttpHeaders.CONTENT_TYPE, "application/zip")
          .addHeader("Content-Transfer-Encoding", "base64")
          .setEntity(new ByteArrayEntity(base64Zip))
          .build();

      try (CloseableHttpResponse response = httpClient.execute(request)) {
        if (response.getStatusLine().getStatusCode() != 200) {
          log.warn("Call to validation service on {} returned status {}, with message:\n{}",
              validatorUrl,
              response.getStatusLine().getStatusCode(),
              response.getStatusLine().getReasonPhrase());
        }

        OperationOutcome operationOutcome = fhirContext.newJsonParser()
            .parseResource(OperationOutcome.class, response.getEntity().getContent());
        String html = operationOutcome.getIssueFirstRep().getDiagnostics();
        Whitelist whitelist = Whitelist.relaxed()
            .addTags("hr")
            .addAttributes("tr", "bgcolor");
        String safeHtml = Jsoup.clean(html, whitelist);

        return ReportsDTO.builder()
            .contentType(ContentType.HTML)
            .reportType(ReportType.VALIDATION)
            .request(validatorUrl)
            .response(safeHtml)
            .build();
      } catch (HttpClientErrorException e) {
        return ReportsDTO.builder()
            .contentType(ContentType.HTML)
            .reportType(ReportType.VALIDATION)
            .request(validatorUrl)
            .response("Failed to request validation: " + e.getMessage())
            .build();
      }
    } catch (IOException e) {
      throw new InternalErrorException(
          "Creating Reports: Unable to create resource bundle for validation", e);
    } catch (ResourceAccessException e) {
      return ReportsDTO.builder()
          .contentType(ContentType.HTML)
          .reportType(ReportType.VALIDATION)
          .request("")
          .response("Creating Reports: Unable to contact validation service: " + e.getMessage())
          .build();
    }
  }

}
