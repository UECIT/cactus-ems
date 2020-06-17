package uk.nhs.ctp.audit.sqs;

import uk.nhs.ctp.audit.model.AuditSession;

public interface AuditSender {
    void sendAudit(AuditSession session);
}
