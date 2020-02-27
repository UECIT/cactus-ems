package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.AuditEntry;

@Repository
public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

}
