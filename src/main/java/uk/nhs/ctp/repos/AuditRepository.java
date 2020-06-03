package uk.nhs.ctp.repos;

import java.util.List;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.Audit;

@Repository
// TODO: delete this quickly
public interface AuditRepository extends PartitionedRepository<Audit, Long> {

  List<Audit> findAllByCaseId(Long caseId);

}
