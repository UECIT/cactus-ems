package uk.nhs.ctp.service;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.hl7.fhir.dstu3.model.codesystems.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.ctp.enums.AuditEntryType;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.Reportable;

@Service
public class ReportService {

  @Autowired
  private Collection<Reportable> reportServices;

  @Autowired
  private AuditService auditService;

  @Autowired
  private ObjectMapper mapper;

  @Value("${reports.server}")
  private String reportsServer;

  @Value("${reports.server.auth.token}")
  private String reportServerToken;

  private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

  public Collection<ReportsDTO> generateReports(
      ReportRequestDTO request) throws JAXBException, JsonProcessingException {

    Collection<ReportsDTO> reports = new ArrayList<>();

    for (Reportable service : reportServices) {
      try {
        reports.add(service.generate(request.clone()));
      } catch (Exception e) {
        LOG.error(MessageFormat.format("Error creating report {0} ",
            service.getClass().getSimpleName().replace("Service", "")), e);
      }

    }

    auditService.createAuditEntry(
        request.getCaseId(), mapper.writeValueAsString(request), mapper.writeValueAsString(reports),
        AuditEntryType.REPORT);

    return reports;
  }

  public Collection<ReportsDTO> generateReports(String encounterRef) {
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

    LOG.info("Sending an http post to: {}", reportsUrl);

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

      ArrayList<ReportsDTO> reports = new ArrayList<>();
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

      return reports;
    } catch (IOException e) {
      throw new InternalErrorException(
          "Creating Reports: Unable to parse response", e);
    }
  }

}
