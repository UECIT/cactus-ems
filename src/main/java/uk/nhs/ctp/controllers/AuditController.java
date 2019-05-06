package uk.nhs.ctp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.service.AuditService;
import uk.nhs.ctp.service.search.AuditSearchRequest;
import uk.nhs.ctp.service.search.AuditSearchResultDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/audit")
public class AuditController {

	@Autowired
	private AuditService auditService;

	@GetMapping(path = "/{id}")
	public @ResponseBody AuditRecord getAudit(@PathVariable Long id) {
		return auditService.getAuditRecordByCase(id);
	}
	
	@PostMapping
	public @ResponseBody Page<AuditSearchResultDTO> getAudit(@RequestBody AuditSearchRequest request) {
		return auditService.search(request);
	}

}
