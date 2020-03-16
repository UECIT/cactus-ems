package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.repos.ObservationRepository;
import uk.nhs.ctp.transform.ObservationTransformer;

@Service
@RequiredArgsConstructor
public class ObservationService {

  private final ObservationRepository observationRepository;
  private final ObservationTransformer observationTransformer;

  @Transactional
  public Observation getOne(IdType id) {
    CaseObservation obs = observationRepository.getOne(id.getIdPartAsLong());
    return observationTransformer.transform(obs);
  }

  @Transactional
  public List<Observation> getByCaseId(long caseId) {
    return observationRepository.findAllByCaseEntityId(caseId)
        .stream()
        .map(observationTransformer::transform)
        .collect(Collectors.toUnmodifiableList());
  }
}
