package uk.nhs.ctp.repos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.ctp.entities.CaseObservation;

@Repository
public interface ObservationRepository extends JpaRepository<CaseObservation, Long> {
  List<CaseObservation> findAllByCaseEntityId(long caseId);
}
