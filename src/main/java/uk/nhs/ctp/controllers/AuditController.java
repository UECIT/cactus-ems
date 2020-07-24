package uk.nhs.ctp.controllers;

import static org.thymeleaf.util.StringUtils.isEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import uk.nhs.ctp.auditFinder.AuditFinder;
import uk.nhs.ctp.auditFinder.model.AuditInteraction;
import uk.nhs.ctp.auditFinder.model.AuditValidationRequest;
import uk.nhs.ctp.caseSearch.CaseSearchRequest;
import uk.nhs.ctp.caseSearch.CaseSearchResultDTO;
import uk.nhs.ctp.caseSearch.CaseSearchService;
import uk.nhs.ctp.tkwvalidation.ValidationService;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

@CrossOrigin
@RestController
@RequestMapping(path = "/audit")
@RequiredArgsConstructor
@Slf4j
public class AuditController {

  private final ObjectMapper mapper;
  private final AuditFinder auditFinder;
  private final CaseSearchService caseSearchService;
  private final ValidationService validationService;

  /**
   * Retrieve all audit sessions for a given case ID. Because this search requires the case id
   * property existing, this will return sessions during a triage, namely: $evaluate.
   *
   * @param id Case ID to search on
   * @return list of Audit Sessions
   */
  @GetMapping(path = "/{id}")
  public String getAudit(@PathVariable Long id) throws IOException {
    return mapper.writeValueAsString(auditFinder.findAllEmsEncountersByCaseId(id.toString()));
  }

	@GetMapping(path = "/interactions")
	public List<AuditInteraction> getAuditInteractions() {
		return auditFinder.groupInteractions(auditFinder.findInteractions());
  }

	private List<AuditSession> findAudits(AuditValidationRequest request) {
		if (isEmpty(request.getInteractionId())) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Must specify an interactionId");
		}

		var audits = auditFinder.findAllEncountersByOperationTypeAndInteractionId(
				request.getType(),
				request.getInteractionId());
		if (audits.isEmpty()) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "No audits found with given interactionId");
		}

		return audits;
	}

	@PostMapping(path = "/validate")
	public void validate(@RequestBody AuditValidationRequest request) throws IOException {
		validationService.validateAudits(
				findAudits(request),
				request.getType(),
				request.getInstanceBaseUrl());
  }


  @PostMapping(path = "/download")
  public ResponseEntity<byte[]> download(@RequestBody AuditValidationRequest request)
      throws IOException {

    var audits = findAudits(request);

    var zipData = validationService.getZipData(audits, request.getType());
    AuditMetadata metadata = validationService
        .getAuditMetadata(audits, request.getType(), request.getInstanceBaseUrl());

    BodyBuilder response = ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("application/zip"))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            // TODO this filename could be based on the interaction ID
            "attachment; filename=\"" + UUID.randomUUID().toString() + ".zip\"")
        .headers(metadata.toHeaders());

    return response.body(zipData);
  }

  @PostMapping
  public Page<CaseSearchResultDTO> getAudit(@RequestBody CaseSearchRequest request) {
    return caseSearchService.search(request);
  }

}
