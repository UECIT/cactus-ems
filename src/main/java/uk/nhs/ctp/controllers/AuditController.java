package uk.nhs.ctp.controllers;

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

import uk.nhs.ctp.entities.Audit;
import uk.nhs.ctp.repos.AuditRepository;
import uk.nhs.ctp.service.AuditService;
import uk.nhs.ctp.service.search.AuditSearchRequest;
import uk.nhs.ctp.service.search.AuditSearchResultDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/audit")
@RequiredArgsConstructor
public class AuditController {

	private final AuditRepository auditRepository;
	private final AuditService auditService;

	@GetMapping(path = "/{id}")
	public @ResponseBody
	List<Audit> getAudit(@PathVariable Long id) {
		//TODO: CDSCT-139
		return auditRepository.findAllByCaseId(id);
	}
	
	@PostMapping
	public @ResponseBody Page<AuditSearchResultDTO> getAudit(@RequestBody AuditSearchRequest request) {
		return auditService.search(request);
	}

}
