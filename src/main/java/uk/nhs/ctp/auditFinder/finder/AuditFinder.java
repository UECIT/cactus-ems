package uk.nhs.ctp.auditFinder.finder;

import java.util.List;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;

public interface AuditFinder {
    List<AuditSession> findAllEncountersByOperationTypeAndInteractionId(
        OperationType operationType,
        String interactionId);

    /**
     * Find all audit sessions based on the caseId
     * @param caseId case id property to search for
     * @return audit sessions matching the case id
     */
    List<AuditSession> findAllEmsEncountersByCaseId(String caseId);

    /**
     * Find all audit sessions that were part of an encounter.
     * Implementations should return all audit sessions that contain that case id property for the current supplier.
     * @return list of audit sessions for all encounters.
     */
    List<AuditSession> findAllEncounters();

    /**
     * Find all audit sessions that were part of a search for a service definition.
     * Implementations should return...
     * @return list of audit sessions for service searches.
     */
    List<AuditSession> findAllServiceSearches();
}
