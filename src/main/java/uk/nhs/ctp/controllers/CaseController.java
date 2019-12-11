package uk.nhs.ctp.controllers;

import static ca.uhn.fhir.rest.param.ParamPrefixEnum.EQUAL;
import static uk.nhs.ctp.utils.DateUtils.calculateAge;

import java.util.List;
import lombok.AllArgsConstructor;
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
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.CdssService;
import uk.nhs.ctp.service.PatientService;
import uk.nhs.ctp.service.TriageService;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionSearchDTO;
import uk.nhs.ctp.service.dto.TriageLaunchDTO;
import uk.nhs.ctp.service.search.SearchParameters;

@CrossOrigin
@RestController
@RequestMapping(path = "/case")
@AllArgsConstructor
public class CaseController {

  private final CdssService cdssService;
  private final TriageService triageService;
  private final CaseRepository caseRepository;
  private final PatientService patientService;

  @PostMapping(path = "/")
  public @ResponseBody
  CdssResponseDTO launchTriage(@RequestBody TriageLaunchDTO requestDTO) throws Exception {
    return triageService.launchTriage(requestDTO);
  }

  @PostMapping(path = "/serviceDefinitions")
  public @ResponseBody
  List<CdssSupplierDTO> getServiceDefinitions(@RequestBody ServiceDefinitionSearchDTO requestDTO) {

    PatientEntity patient = patientService.findById(requestDTO.getPatientId());

    return cdssService
        .queryServiceDefinitions(SearchParameters.builder()
            .query("triage")
            .contextValue("gender", patient.getGender())
            .contextQuantity("age", EQUAL, calculateAge(patient.getDateOfBirth()))
            .contextValue("user", requestDTO.getSettings().getUserType().getCode())
            .contextValue("setting", requestDTO.getSettings().getSetting().getCode())
            .contextValue("task", requestDTO.getSettings().getUserTaskContext().getCode())
            .jurisdiction(requestDTO.getSettings().getJurisdiction().getCode())
            .build()
        );
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
