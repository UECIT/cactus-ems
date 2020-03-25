package uk.nhs.ctp.repos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.CaseCarePlan;

@Repository
public interface CarePlanRepository extends JpaRepository<CaseCarePlan, Long> {
  List<CaseCarePlan> findAllByCaseEntityId(long caseId);
}
