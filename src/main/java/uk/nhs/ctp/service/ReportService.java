package uk.nhs.ctp.service;

import ca.uhn.fhir.parser.DataFormatException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.IdType;
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
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.auditFinder.finder.AuditFinder;
import uk.nhs.ctp.enums.ContentType;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.Reportable;
import uk.nhs.ctp.tkwvalidation.AuditDispatcher;
import uk.nhs.ctp.tkwvalidation.ValidationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

  private final Collection<Reportable> reportServices;
  private final ValidationService validationService;
  private final ObjectMapper mapper;
  private final AuditFinder auditFinder;
  private final AuditDispatcher auditDispatcher;

  @Value("${reports.server}")
  private String reportsServer;

  @Value("${reports.server.auth.token}")
  private String reportServerToken;

  public Collection<ReportsDTO> generateReports(ReportRequestDTO request) {

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
    ReportsDTO reportsDTO = ReportsDTO.builder()
        .contentType(ContentType.HTML)
        .reportType(ReportType.VALIDATION)
        .request(validationService.getValidationUrl())
        .build();

    var caseId = new IdType(encounterRef).getIdPart();
    var audits = auditFinder.findAllEmsEncountersByCaseId(caseId);

    try {
      validationService.validateAudits(audits, OperationType.ENCOUNTER, "");
      return reportsDTO;
    } catch (DataFormatException e) {
      reportsDTO.setResponse("Error parsing response");
      return reportsDTO;
    } catch (HttpClientErrorException e) {
      reportsDTO.setResponse("Failed to request validation: " + e.getMessage());
      return reportsDTO;
    } catch (ResourceAccessException e) {
      reportsDTO.setResponse("Creating Reports: Unable to contact validation service: " + e.getMessage());
      return reportsDTO;
    } catch (IOException e) {
      reportsDTO.setResponse("Creating Reports: Unable to create zip file: " + e.getMessage());
      return reportsDTO;
    }
  }

}
