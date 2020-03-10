package uk.nhs.ctp.service;

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
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
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

    ResponseEntity<String> response = template
        .exchange(reportsUrl, HttpMethod.POST, null, String.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      throw new InternalErrorException(
          "Creating Reports: Unexpected response status: " + response.getStatusCode());
    }

    try {
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
    } catch (IOException e) {
      throw new InternalErrorException(
          "Creating Reports: Unable to parse response", e);
    }

    reports.add(validate(encounterRef));

    return reports;
  }

  public ReportsDTO validate(String encounterRef) {
    try {
      byte[] zipData = validationService
          .zipResources(new Reference(encounterRef).getReferenceElement().getIdPartAsLong());

      byte[] encodedZip = Base64.getEncoder().encode(zipData);
      RestTemplate template = new RestTemplate();

      ResponseEntity<String> responseHtml = template
          .exchange(reportValidationServer, HttpMethod.POST, new HttpEntity<>(encodedZip),
              String.class);

      if (!responseHtml.getStatusCode().is2xxSuccessful()) {
        log.warn("Call to validation service on {} returned status {}, with message:\n{}",
            reportValidationServer,
            responseHtml.getStatusCode().value(),
            responseHtml.getStatusCode().getReasonPhrase());
      }

      return ReportsDTO.builder()
          .contentType(ContentType.HTML)
          .reportType(ReportType.VALIDATION)
          .request(reportValidationServer)
          .response(responseHtml.getBody())
          .build();

    } catch (IOException e) {
      throw new InternalErrorException(
          "Creating Reports: Unable to create resource bundle for validation", e);
    }
    catch (ResourceAccessException e) { //Temporary until we have an endpoint
      log.warn("Unable to invoke reports validation on {}", reportValidationServer);
      return ReportsDTO.builder()
          .contentType(ContentType.HTML)
          .reportType(ReportType.VALIDATION)
          .request(reportValidationServer)
          .response(e.getMessage())
          .build();
    }
  }

}
