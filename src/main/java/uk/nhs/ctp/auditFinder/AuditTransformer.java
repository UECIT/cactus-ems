package uk.nhs.ctp.auditFinder;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toUnmodifiableList;
import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;

import java.time.Instant;
import java.util.List;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.audit.model.AuditProperties;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.auditFinder.model.AuditInteraction;

@Component
public class AuditTransformer {

  public List<AuditInteraction> groupAndTransformInteractions(List<AuditSession> auditSessions) {
    return auditSessions.stream()
        .collect(groupingBy(this::getKey, minBy(comparing(AuditSession::getCreatedDate))))
        .entrySet()
        .stream()
        .map(pair -> new AuditInteraction(
            OperationType.fromName(pair.getKey().getFirst()),
            pair.getKey().getSecond(),
            pair.getValue().map(AuditSession::getCreatedDate).map(Instant::toString).orElseThrow()))
        .sorted(comparing(AuditInteraction::getStartedAt).reversed())
        .collect(toUnmodifiableList());
  }

  private Pair<String, String> getKey(AuditSession interactionAudit) {
    var properties = interactionAudit.getAdditionalProperties();
    return Pair.of(properties.get(AuditProperties.OPERATION_TYPE), properties.get(INTERACTION_ID));
  }

}
