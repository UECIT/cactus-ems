package uk.nhs.ctp.auditFinder.finder;

import uk.nhs.ctp.audit.model.AuditSession;

import java.util.List;

public interface AuditFinder {
    List<AuditSession> findAll(Long caseId);
}
