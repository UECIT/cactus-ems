package uk.nhs.ctp.controllers;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.AuditFinderService;
import uk.nhs.ctp.caseSearch.CaseSearchRequest;
import uk.nhs.ctp.caseSearch.CaseSearchResultDTO;
import uk.nhs.ctp.caseSearch.CaseSearchService;

@CrossOrigin
@RestController
@RequestMapping(path = "/audit")
@RequiredArgsConstructor
public class AuditController {

	private final AuditFinderService auditFinder;
	private final CaseSearchService caseSearchService;

	@GetMapping(path = "/{id}")
	public @ResponseBody
	List<AuditSession> getAudit(@PathVariable Long id) throws IOException {
		return auditFinder.findAll(id);
	}
	
	@PostMapping
	public @ResponseBody Page<CaseSearchResultDTO> getAudit(@RequestBody CaseSearchRequest request) {
		return caseSearchService.search(request);
	}

}
