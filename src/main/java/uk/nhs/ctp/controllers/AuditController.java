package uk.nhs.ctp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.auditFinder.AuditFinderService;
import uk.nhs.ctp.caseSearch.CaseSearchRequest;
import uk.nhs.ctp.caseSearch.CaseSearchResultDTO;
import uk.nhs.ctp.caseSearch.CaseSearchService;

@CrossOrigin
@RestController
@RequestMapping(path = "/audit")
@RequiredArgsConstructor
public class AuditController {

	@Qualifier("enhanced")
	private final ObjectMapper mapper;
	private final AuditFinderService auditFinder;
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
