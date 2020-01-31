package uk.nhs.ctp.service.encounter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Encounter;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.repos.AuditRecordRepository;
import uk.nhs.ctp.repos.CaseRepository;

@Service
@AllArgsConstructor
@Slf4j
public class EncounterService {

  private EncounterTransformer encounterTransformer;
  private CaseRepository caseRepository;
  private AuditRecordRepository auditRecordRepository;

  public Encounter getEncounter(Long caseId) {
    Cases triageCase = caseRepository.findOne(caseId);
    AuditRecord auditRecord = auditRecordRepository.findByCaseId(caseId);
    Encounter encounter = encounterTransformer.transform(triageCase, auditRecord);
    encounter.setId(caseId.toString());
    return encounter;
  }
}
