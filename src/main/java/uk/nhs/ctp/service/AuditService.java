package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.enums.AuditEntryType;
import uk.nhs.ctp.repos.AuditEntryRepository;
import uk.nhs.ctp.repos.AuditRecordRepository;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.search.AuditSearchRequest;
import uk.nhs.ctp.service.search.AuditSearchResultDTO;

@Service
@AllArgsConstructor
public class AuditService {

  private AuditRecordRepository auditRepository;
  private AuditEntryRepository auditEntryRepository;
  private ObjectMapper mapper;
  private FhirContext fhirContext;

  // create new audit record.
  public AuditRecord createNewAudit(Long caseId) {

    // check if audit already exists and create if not.
    AuditRecord currentAuditRecord = auditRepository.findByEncounterId(caseId);
    if (currentAuditRecord != null) {
      return currentAuditRecord;
    } else {
      AuditRecord newAuditRecord = new AuditRecord();
      // update standard fields......
      newAuditRecord.setEncounterId(caseId);
      newAuditRecord.setTriageComplete(false);
      newAuditRecord.setCreatedDate(new Date());
      newAuditRecord.setAuditEntries(new ArrayList<>());
      return auditRepository.saveAndFlush(newAuditRecord);
    }
  }

  public AuditRecord updateAuditEntry(AuditRecord auditRecord, CdssRequestDTO request,
      CdssResponseDTO response, List<Resource> contained) throws JsonProcessingException {

    // check auditRecord exists
    AuditRecord newAuditRecord = auditRepository.findOne(auditRecord.getId());

    // create audit Entry and add it to the audit record.
    AuditEntry newAuditEntry = newAuditRecord.getAuditEntries()
        .get(newAuditRecord.getAuditEntries().size() - 1);

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

  public AuditRecord updateAuditEntry(Long caseId, String request, String response)
      throws JsonProcessingException {

    // check auditRecord exists
    AuditRecord newAuditRecord = auditRepository.findByEncounterId(caseId);

    // create audit Entry and add it to the audit record.
    AuditEntry newAuditEntry = newAuditRecord.getAuditEntries()
        .get(newAuditRecord.getAuditEntries().size() - 1);

    // based data passed in, decide which type of audit entry to create.
    newAuditEntry.setType(AuditEntryType.REQUEST);
    newAuditEntry.setCdssQuestionnaireRequest(request);
    newAuditEntry.setCdssQuestionnaireResponse(response);

    newAuditRecord.getAuditEntries().add(newAuditEntry);
    return auditRepository.saveAndFlush(newAuditRecord);
  }

  public AuditRecord createAuditEntry(Long caseId, String request, String response,
      AuditEntryType auditEntryType) throws JsonProcessingException {
    AuditRecord currentAuditRecord = auditRepository.findByEncounterId(caseId);
    AuditEntry newAuditEntry = new AuditEntry();
    newAuditEntry.setType(auditEntryType);
    newAuditEntry.setCreatedDate(new Date());

    try {
      IParser fhirParser = fhirContext.newJsonParser();
      Resource resource = (Resource) fhirParser.parseResource(request);
      if (resource.getResourceType().equals(ResourceType.Parameters)) {
        Bundle bundle = new Bundle();
        bundle.addEntry().setResource(resource);
        request = fhirParser.encodeResourceToString(bundle);
      }
    } catch (Exception e) {
      // TODO: handle exception
    }

    newAuditEntry.setCdssServiceDefinitionRequest(request);
    newAuditEntry.setCdssServiceDefinitionResponse(response);

    newAuditEntry.setAuditRecord(currentAuditRecord);

    currentAuditRecord.getAuditEntries().add(newAuditEntry);
    return auditRepository.saveAndFlush(currentAuditRecord);
  }

  // get audit record
  public AuditRecord getAuditRecord(Long id) {
    return auditRepository.findOne(id);
  }

  public AuditRecord getAuditRecordByCase(Long caseId) {
    return auditRepository.findByEncounterId(caseId);
  }

  public List<AuditEntry> getAuditEntries(Long caseId, AuditEntryType... types) {
    return auditEntryRepository
        .findByAuditRecord_CaseIdAndTypeInOrderById(caseId, Arrays.asList(types));
  }

  public Page<AuditSearchResultDTO> search(AuditSearchRequest request) {
    return auditRepository.search(
        request.getFrom(), request.getTo(),
        request.isIncludeClosed(), request.isIncludeIncomplete(), request);
  }

  private String createContainedAudit(List<Resource> contained) {
    if (contained.isEmpty()) {
      return null;
    }

    IParser fhirParser = fhirContext.newJsonParser();
    return fhirParser.encodeResourceToString(
        new Bundle().setType(BundleType.COLLECTION).setEntry(contained.stream().map(resource ->
            new BundleEntryComponent().setResource(resource)).collect(Collectors.toList())));
  }

}
