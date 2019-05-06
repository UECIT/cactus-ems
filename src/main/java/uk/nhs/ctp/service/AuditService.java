package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.enums.AuditEntryType;
import uk.nhs.ctp.repos.AuditRecordRepository;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.search.AuditSearchRequest;
import uk.nhs.ctp.service.search.AuditSearchResultDTO;

@Service
public class AuditService {

	@Autowired
	private AuditRecordRepository auditRepository;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private IParser fhirParser; 
	
	// create new audit record.
	public AuditRecord createNewAudit(Long caseId) {

		// check if audit already exists and create if not.
		AuditRecord currentAuditRecord = auditRepository.findByCaseId(caseId);
		if (currentAuditRecord != null) {
			return currentAuditRecord;
		} else {
			AuditRecord newAuditRecord = new AuditRecord();
			// update standard fields......
			newAuditRecord.setCaseId(caseId);
			newAuditRecord.setTriageComplete(false);
			newAuditRecord.setCreatedDate(new Date());
			newAuditRecord.setAuditEntries(new ArrayList<AuditEntry>());
			return auditRepository.saveAndFlush(newAuditRecord);
		}
	}

	public AuditRecord updateAuditEntry(AuditRecord auditRecord, CdssRequestDTO request, 
			CdssResponseDTO response, List<Resource> contained)	throws JsonProcessingException {

		// check auditRecord exists
		AuditRecord newAuditRecord = auditRepository.findOne(auditRecord.getId());

		// create audit Entry and add it to the audit record.
		AuditEntry newAuditEntry = newAuditRecord.getAuditEntries().get(newAuditRecord.getAuditEntries().size() - 1);

		// based data passed in, decide which type of audit entry to create.
		newAuditEntry.setType(AuditEntryType.REQUEST);
		newAuditEntry.setTestHarnessRequest(mapper.writeValueAsString(request));
		newAuditEntry.setTestHarnessResponse(mapper.writeValueAsString(response));
		newAuditEntry.setContained(createContainedAudit(contained));
		
		
		if (response.getResult() != null && response.getSwitchTrigger() == null) {
			newAuditRecord.setTriageComplete(true);
			newAuditRecord.setClosedDate(new Date());
		}

		newAuditRecord.getAuditEntries().add(newAuditEntry);
		return auditRepository.saveAndFlush(newAuditRecord);
	}

	public AuditRecord updateAuditEntry(Long caseId, String request, String response) throws JsonProcessingException {

		// check auditRecord exists
		AuditRecord newAuditRecord = auditRepository.findByCaseId(caseId);

		// create audit Entry and add it to the audit record.
		AuditEntry newAuditEntry = newAuditRecord.getAuditEntries().get(newAuditRecord.getAuditEntries().size() - 1);

		// based data passed in, decide which type of audit entry to create.
		newAuditEntry.setType(AuditEntryType.REQUEST);
		newAuditEntry.setCdssQuestionnaireRequest(request);
		newAuditEntry.setCdssQuestionnaireResponse(response);

		newAuditRecord.getAuditEntries().add(newAuditEntry);
		return auditRepository.saveAndFlush(newAuditRecord);
	}

	public AuditRecord createAuditEntry(Long caseId, String request, String response) throws JsonProcessingException {
		AuditRecord currentAuditRecord = auditRepository.findByCaseId(caseId);
		AuditEntry newAuditEntry = new AuditEntry();
		newAuditEntry.setType(AuditEntryType.RESULT);
		newAuditEntry.setAuditRecordId(currentAuditRecord.getId());
		newAuditEntry.setCreatedDate(new Date());

		newAuditEntry.setCdssServiceDefinitionRequest(request);
		newAuditEntry.setCdssServiceDefinitionResponse(response);

		currentAuditRecord.getAuditEntries().add(newAuditEntry);
		return auditRepository.saveAndFlush(currentAuditRecord);
	}

	// get audit record
	public AuditRecord getAuditRecord(Long id) {
		return auditRepository.findOne(id);
	}
	
	public AuditRecord getAuditRecordByCase(Long caseId) {
		return auditRepository.findByCaseId(caseId);
	}

	public Page<AuditSearchResultDTO> search(AuditSearchRequest request) {
		return auditRepository.search(
				request.getFrom(), request.getTo(), 
				request.isIncludeClosed(), request.isIncludeIncomplete(), request);
	}
	
	private String createContainedAudit(List<Resource> contained) {
		
        return !contained.isEmpty() ? 
                fhirParser.encodeResourceToString(new Bundle().setEntry(contained.stream().map(resource -> 
                    new BundleEntryComponent().setResource(resource)).collect(Collectors.toList()))) : null;
	}

}
