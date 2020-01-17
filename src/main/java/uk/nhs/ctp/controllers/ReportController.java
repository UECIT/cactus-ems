package uk.nhs.ctp.controllers;

import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collection;
import javax.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.service.ReportService;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportsDTO;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/report")
public class ReportController {

  private final ReportService reportService;
  private final IParser fhirParser;

  @Value("${reports.enabled}")
  private Boolean reportsEnabled;

  @GetMapping("enabled")
  public Boolean getReportsEnabled() {
    return reportsEnabled;
  }

  @PostMapping
  public @ResponseBody
  Collection<ReportsDTO> getReport(
      @RequestBody ReportRequestDTO reportRequestDTO)
      throws JAXBException, JsonProcessingException {

    reportRequestDTO.setFhirParser(fhirParser);
    return reportService.generateReports(reportRequestDTO);
  }

  @PostMapping(path = "/encounter")
  public @ResponseBody
  ReportsDTO getReports(@RequestBody String encounterRef) {
    return reportService.generateReports(encounterRef);
  }

}