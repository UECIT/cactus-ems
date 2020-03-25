package uk.nhs.ctp.repos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.CompositionEntity;

@Repository
public interface CompositionRepository extends JpaRepository<CompositionEntity, Long> {
  List<CompositionEntity> findAllByCaseEntityId(long caseId);
}
