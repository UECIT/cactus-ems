package uk.nhs.ctp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import uk.nhs.ctp.service.TriageService;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.TriageLaunchDTO;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@CrossOrigin
@RestController
@RequestMapping(path = "/case")
public class CaseController {

  @Autowired
  private TriageService triageService;

  @Autowired
  private CaseRepository caseRepository;

  @PostMapping(path = "/")
  public @ResponseBody
  CdssResponseDTO launchTriage(@RequestBody TriageLaunchDTO requestDTO) throws Exception {
    return triageService.launchTriage(requestDTO);
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
    return ErrorHandlingUtils.checkEntityExists(
        caseRepository.findById(id), "Case");
  }
}
