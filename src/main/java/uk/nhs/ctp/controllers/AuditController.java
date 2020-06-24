package uk.nhs.ctp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.finder.AuditFinder;
import uk.nhs.ctp.caseSearch.CaseSearchRequest;
import uk.nhs.ctp.caseSearch.CaseSearchResultDTO;
import uk.nhs.ctp.caseSearch.CaseSearchService;

@CrossOrigin
@RestController
@RequestMapping(path = "/audit")
@RequiredArgsConstructor
public class AuditController {

	private final ObjectMapper mapper;
	private final AuditFinder auditFinder;
	private final CaseSearchService caseSearchService;

	/**
	 * Retrieve all audit sessions for a given case ID.
	 * Because this search requires the case id property existing, this will return sessions during a triage, namely: $evaluate.
	 * @param id Case ID to search on
	 * @return list of Audit Sessions
	 */
	@GetMapping(path = "/{id}")
	public String getAudit(@PathVariable Long id) throws IOException {
		return mapper.writeValueAsString(auditFinder.findAll(id));
	}

	@GetMapping(path = "/encounters")
	public List<AuditSession> getAuditEncounters() {
		return Collections.singletonList(
				AuditSession.builder()
						.additionalProperty("caseId", "4")
						.createdDate(LocalDateTime.of(2020, Month.JUNE, 19, 15, 30).toInstant(ZoneOffset.UTC))
						.requestOrigin("http://hardcoded-encounter-origin/fhir")
						.build()
		);
	}

	@GetMapping(path = "/servicesearches")
	public List<AuditSession> getAuditServiceSearch() {
		return Collections.singletonList(
				AuditSession.builder()
						.createdDate(LocalDateTime.of(2020, Month.JUNE, 19, 15, 30).toInstant(ZoneOffset.UTC))
						.requestOrigin("http://hardcoded-search-origin/fhir")
						.build()
		);
	}
	
	@PostMapping
	public Page<CaseSearchResultDTO> getAudit(@RequestBody CaseSearchRequest request) {
		return caseSearchService.search(request);
	}

}
