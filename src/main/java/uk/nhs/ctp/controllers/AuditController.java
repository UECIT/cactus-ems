package uk.nhs.ctp.controllers;

import static org.thymeleaf.util.StringUtils.isEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.finder.AuditFinder;
import uk.nhs.ctp.auditFinder.model.AuditValidationRequest;
import uk.nhs.ctp.caseSearch.CaseSearchRequest;
import uk.nhs.ctp.caseSearch.CaseSearchResultDTO;
import uk.nhs.ctp.caseSearch.CaseSearchService;
import uk.nhs.ctp.tkwvalidation.ValidationService;

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
	 * Retrieve all audit sessions for a given case ID.
	 * Because this search requires the case id property existing, this will return sessions during a triage, namely: $evaluate.
	 * @param id Case ID to search on
	 * @return list of Audit Sessions
	 */
	@GetMapping(path = "/{id}")
	public String getAudit(@PathVariable Long id) throws IOException {
		return mapper.writeValueAsString(auditFinder.findAllEmsEncountersByCaseId(id.toString()));
	}

	@GetMapping(path = "/encounters")
	public List<AuditSession> getAuditEncounters() {
		return auditFinder.findAllEncounters();
	}

	@GetMapping(path = "/servicesearches")
	public List<AuditSession> getAuditServiceSearch() {
		return auditFinder.findAllServiceSearches();
	}

	@PostMapping(path = "/validate")
	public void validate(@RequestBody AuditValidationRequest request) throws IOException {
		// TODO CDSCT-400: unify different queries

		if (!isEmpty(request.getCaseId())) {
			// client must be asking for evaluate requests
			var audits = auditFinder.findAllEncountersByCaseId(request.getCaseId());
			validationService.validateAudits(
					audits,
					request.getType(),
					request.getInstanceBaseUrl());
			return;
		}

		if (!isEmpty(request.getSearchAuditId())) {
			var audit = auditFinder.findByAuditId(request.getSearchAuditId())
					.orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
			validationService.validateAudits(
					Collections.singletonList(audit),
					request.getType(),
					request.getInstanceBaseUrl());
			return;
		}

		throw new HttpClientErrorException(
				HttpStatus.BAD_REQUEST,
				"Must specify either caseId or searchAuditId");
	}
	
	@PostMapping
	public Page<CaseSearchResultDTO> getAudit(@RequestBody CaseSearchRequest request) {
		return caseSearchService.search(request);
	}

}
