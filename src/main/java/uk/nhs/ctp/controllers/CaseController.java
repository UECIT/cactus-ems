package uk.nhs.ctp.controllers;

import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.CdssService;
import uk.nhs.ctp.service.TriageService;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionSearchDTO;
import uk.nhs.ctp.service.dto.TriageLaunchDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/case")
public class CaseController {

  private final CdssService cdssService;
  private final TriageService triageService;
  private final CaseRepository caseRepository;

  public CaseController(
      CdssService cdssService,
      TriageService triageService,
      CaseRepository caseRepository) {
    this.cdssService = cdssService;
    this.triageService = triageService;
    this.caseRepository = caseRepository;
  }

  @PostMapping(path = "/")
  public @ResponseBody
  CdssResponseDTO launchTriage(@RequestBody TriageLaunchDTO requestDTO) throws Exception {
    return triageService.launchTriage(requestDTO);
  }

  @PostMapping(path = "/serviceDefinitions")
  public @ResponseBody
  List<CdssSupplierDTO> getServiceDefinitions(@RequestBody ServiceDefinitionSearchDTO requestDTO)
      throws Exception {
    // TODO include query parameters based on patient, case and context
    List<CdssSupplierDTO> serviceDefinitions = cdssService
        .queryServiceDefinitions("triage", Collections.emptyMap());

    // Only return first SD for each supplier
    // TODO remove when triage query is finished
    for (CdssSupplierDTO supplierDTO : serviceDefinitions) {
      if (!supplierDTO.getServiceDefinitions().isEmpty()) {
        supplierDTO.setServiceDefinitions(supplierDTO.getServiceDefinitions().subList(0, 1));
      }
    }

    return serviceDefinitions;
  }

  @PutMapping(path = "/")
  public @ResponseBody
  CdssResponseDTO sendTriageRequest(@RequestBody CdssRequestDTO requestDTO) throws Exception {
    return triageService.processTriageRequest(requestDTO);
  }

  @PutMapping(path = "/back")
  public @ResponseBody
  CdssResponseDTO amendTriageRequest(@RequestBody CdssRequestDTO requestDTO) throws Exception {
    return triageService.processTriageAmendRequest(requestDTO);
  }

  @GetMapping(path = "/{id}")
  public @ResponseBody
  Cases getCase(@PathVariable Long id) {
    return caseRepository.findOne(id);
  }
}
