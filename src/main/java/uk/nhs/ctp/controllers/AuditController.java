package uk.nhs.ctp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uk.nhs.ctp.auditFinder.finder.AuditFinder;
import uk.nhs.ctp.caseSearch.CaseSearchRequest;
import uk.nhs.ctp.caseSearch.CaseSearchResultDTO;
import uk.nhs.ctp.caseSearch.CaseSearchService;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping(path = "/audit")
@RequiredArgsConstructor
public class AuditController {

	private final ObjectMapper mapper;
	private final AuditFinder auditFinder;
	private final CaseSearchService caseSearchService;

	@GetMapping(path = "/{id}")
	public String getAudit(@PathVariable Long id) throws IOException {
		return mapper.writeValueAsString(auditFinder.findAll(id));
	}
	
	@PostMapping
	public Page<CaseSearchResultDTO> getAudit(@RequestBody CaseSearchRequest request) {
		return caseSearchService.search(request);
	}

}
