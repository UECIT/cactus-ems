package uk.nhs.ctp.repos;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.service.search.AuditSearchResultDTO;

@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {

//	@Query("SELECT a FROM audit_record a WHERE a.case_id = :caseId")
//    public AuditRecord findByCaseId(@Param("caseId") Long caseId);

	AuditRecord findByCaseId(Long caseId);

	@Query("SELECT NEW uk.nhs.ctp.service.search.AuditSearchResultDTO(c.id, c.firstName, c.lastName, c.timestamp) " +
		   "FROM Cases c  " +
		   "WHERE c.id IN (" +
		   "	SELECT ar.caseId FROM AuditRecord ar " +
		   "	WHERE (:from IS NULL OR createdDate > :from) " +
		   "	AND (:to IS NULL OR createdDate < :to) " +
		   "	AND (:includeClosed = TRUE OR closedDate IS NULL) " +
		   "	AND (:includeIncomplete = TRUE OR triageComplete = TRUE) " +
		   ")")
	Page<AuditSearchResultDTO> search(@Param("from") Date from, @Param("to") Date to, 
			@Param("includeClosed") boolean includeClosed, 
			@Param("includeIncomplete") boolean includeIncomplete, Pageable pageable);

}
