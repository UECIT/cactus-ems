package uk.nhs.ctp.repos;

import java.util.List;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.CaseObservation;

@Repository
public interface ObservationRepository extends PartitionedRepository<CaseObservation, Long> {
  List<CaseObservation> findAllByCaseEntityId(long caseId);
}
