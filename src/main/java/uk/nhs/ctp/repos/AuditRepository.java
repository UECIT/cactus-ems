package uk.nhs.ctp.repos;

import java.util.List;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.Audit;

@Repository
public interface AuditRepository extends PartitionedRepository<Audit> {

  List<Audit> findAllByCaseId(Long caseId);

}
