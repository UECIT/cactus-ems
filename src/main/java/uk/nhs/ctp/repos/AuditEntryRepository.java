package uk.nhs.ctp.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.enums.AuditEntryType;

@Repository
public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

	List<AuditEntry> findByAuditRecord_CaseIdAndTypeInOrderById(Long caseId, List<AuditEntryType> types);

}
