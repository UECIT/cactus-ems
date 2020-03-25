package uk.nhs.ctp.controllers;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.service.EncounterService;
import uk.nhs.ctp.service.ReportService;
import uk.nhs.ctp.service.dto.EncounterHandoverDTO;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.transform.EncounterReportInputTransformer;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/report")
public class ReportController {

  private final ReportService reportService;
  private final EncounterService encounterService;
  private final EncounterReportInputTransformer encounterReportInputTransformer;
  private final FhirContext fhirContext;

  @Value("${reports.enabled}")
  private Boolean reportsEnabled;

  @GetMapping("enabled")
  public Boolean getReportsEnabled() {
    return reportsEnabled;
  }

  @PostMapping
  @ResponseBody
  public Collection<ReportsDTO> getReport(
      @RequestBody ReportRequestDTO reportRequestDTO)
      throws JAXBException, JsonProcessingException {

    reportRequestDTO.setFhirContext(fhirContext);
    return reportService.generateReports(reportRequestDTO);
  }

  @PostMapping(path = "/encounter")
  @ResponseBody
  public Collection<ReportsDTO> getReports(@RequestBody String encounterRef) {
    return reportService.generateReports(encounterRef);
  }

  @GetMapping(path = "/encounter")
  @ResponseBody
  public EncounterHandoverDTO getEncounterReport(@RequestParam String encounterId) {
    return encounterService.getEncounterReportHandover(new IdType(encounterId));
  }

  @GetMapping(path = "/search")
  @ResponseBody
  public List<EncounterHandoverDTO> findEncountersByPatient(@RequestParam String nhsNumber) {
    return encounterService.searchEncounterIdsByPatientNhsNumber(nhsNumber);
  }

}