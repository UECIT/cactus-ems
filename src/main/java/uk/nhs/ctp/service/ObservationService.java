package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.service.fhir.StorageService;

@Service
@RequiredArgsConstructor
public class ObservationService {

  private final StorageService storageService;
  private final CaseService caseService;

  public Observation getOne(IdType id) {
    // Delegate to the FHIR Server
    return storageService.findResource(id);
  }

  @Transactional
  public List<Observation> getByCaseId(long caseId) {
    // Delegate to the FHIR Server
    return caseService.getCaseParameters(caseId).stream()
        // Don't bring back 'soft-deleted' Observations
        .filter(param -> !param.isDeleted())
        .map(CaseParameter::getReference)
        .map(IdType::new)
        .filter(id -> id.getResourceType().equals(ResourceType.Observation.toString()))
        .map(idType -> (Observation) storageService.findResource(idType))
        .collect(Collectors.toUnmodifiableList());
  }
}
