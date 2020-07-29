package uk.nhs.ctp.controllers;

import static java.util.Collections.emptyList;
import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;
import static uk.nhs.cactus.common.audit.model.AuditProperties.OPERATION_TYPE;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cactus.common.audit.AuditService;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.service.CaseService;
import uk.nhs.ctp.service.CdssService;
import uk.nhs.ctp.service.ReferralRequestService;
import uk.nhs.ctp.service.TriageService;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.SelectedServiceRequestDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionSearchDTO;
import uk.nhs.ctp.service.dto.TriageLaunchDTO;
import uk.nhs.ctp.service.search.SearchParametersTransformer;

@CrossOrigin
@RestController
@RequestMapping(path = "/case")
@AllArgsConstructor
public class CaseController {

  private final CdssService cdssService;
  private final TriageService triageService;
  private final CaseService caseService;
  private final AuditService auditService;
  private final ReferralRequestService referralRequestService;

  private final SearchParametersTransformer searchParametersTransformer;

  @PostMapping(path = "/")
  public @ResponseBody
  Long launchTriage(@RequestBody TriageLaunchDTO requestDTO) {
    if (StringUtils.isNotEmpty(requestDTO.getEncounterId())) {
      auditService.addAuditProperty(OPERATION_TYPE, OperationType.ENCOUNTER_REPORT.getName());
      auditService.addAuditProperty(INTERACTION_ID, requestDTO.getEncounterId());
    }
    return triageService.launchTriage(requestDTO);
  }

  @PostMapping(path = "/serviceDefinitions")
  public @ResponseBody
  List<CdssSupplierDTO> getServiceDefinitions(@RequestBody ServiceDefinitionSearchDTO requestDTO) {
    var params = searchParametersTransformer
        .transform(emptyList(), requestDTO.getSettings(), requestDTO.getPatientId());
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.SERVICE_SEARCH.getName());
    auditService.addAuditProperty(INTERACTION_ID, UUID.randomUUID().toString());
    return cdssService.queryServiceDefinitions(params);
  }

  @PutMapping(path = "/")
  public @ResponseBody
  CdssResponseDTO progressTriage(@RequestBody CdssRequestDTO requestDTO) throws Exception {
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.ENCOUNTER.getName());
    auditService.addAuditProperty(INTERACTION_ID, requestDTO.getCaseId().toString());
    return triageService.progressTriage(requestDTO);
  }

  @PutMapping(path = "/back")
  public @ResponseBody
  CdssResponseDTO amendTriage(@RequestBody CdssRequestDTO requestDTO) throws Exception {
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.ENCOUNTER.getName());
    auditService.addAuditProperty(INTERACTION_ID, requestDTO.getCaseId().toString());
    return triageService.amendTriage(requestDTO);
  }

  @GetMapping(path = "/{id}")
  public @ResponseBody
  Cases getCase(@PathVariable Long id) {
    return caseService.findCase(id);
  }

  @PutMapping(path = "/selectedService")
  public @ResponseBody
  void updateSelectedService(@RequestBody SelectedServiceRequestDTO requestDTO) {
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.ENCOUNTER.getName());
    auditService.addAuditProperty(INTERACTION_ID, requestDTO.getCaseId().toString());
    referralRequestService.updateServiceRequested(requestDTO);
  }
}
