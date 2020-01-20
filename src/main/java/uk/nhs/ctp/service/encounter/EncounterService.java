package uk.nhs.ctp.service.encounter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.repos.AuditRecordRepository;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.factory.ReferenceStorageServiceFactory;

@Service
@AllArgsConstructor
@Slf4j
public class EncounterService {

  private EncounterTransformer encounterTransformer;
  private ReferenceStorageServiceFactory storageServiceFactory;

  private CaseRepository caseRepository;
  private AuditRecordRepository auditRecordRepository;

  public String createEncounter(Cases triageCase) {
    Encounter encounter = encounterTransformer.transform(triageCase, null);
    var storageService = storageServiceFactory.load();
    Reference encounterRef = storageService.store(encounter);
    String encounterId = toId(encounterRef);
    log.info("Created new Encounter {}", encounterId);
    return encounterId;
  }

  private static String toId(Reference encounterRef) {
    if (encounterRef.hasReference()) {
      return encounterRef.getId();
    } else if (encounterRef.getResource() != null) {
      return encounterRef.getResource().getIdElement().toString();
    }
    return null;
  }

  public void updateEncounter(Long caseId) {
    Cases triageCase = caseRepository.findOne(caseId);
    AuditRecord auditRecord = auditRecordRepository.findByCaseId(caseId);

    updateEncounter(triageCase, auditRecord);
  }

  public void updateEncounter(Cases triageCase, AuditRecord auditRecord) {
    Encounter encounter = encounterTransformer.transform(triageCase, auditRecord);
    var storageService = storageServiceFactory.load();
    storageService.updateExternal(encounter);

    log.info("Updated encounter {} for case {}", encounter.getId(), triageCase.getId());
  }
}
