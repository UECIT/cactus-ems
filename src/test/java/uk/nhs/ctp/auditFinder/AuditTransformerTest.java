package uk.nhs.ctp.auditFinder;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.auditFinder.model.AuditInteraction;

public class AuditTransformerTest {

  private final AuditTransformer auditTransformer = new AuditTransformer();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void groupInteractions_withEmptyList_returnsEmpty() {
    assertThat(auditTransformer.groupAndTransformInteractions(emptyList()), empty());
  }

  @Test
  public void groupInteractions_withInvalidOperationType_fails() {
    final var creationDate = Instant.parse("2019-08-22T12:11:54Z");

    var encounter1Audit1 = AuditSession.builder()
        .createdDate(creationDate)
        .additionalProperty("operation", "invalid_operation_type")
        .additionalProperty("interactionId", "1")
        .build();

    var auditSessions = List.of(encounter1Audit1);

    expectedException.expect(IllegalArgumentException.class);
    auditTransformer.groupAndTransformInteractions(auditSessions);
  }

  @Test
  public void groupInteractions_returnsGroups() {
    final var creationDate1 = Instant.parse("2019-08-22T12:11:54Z");
    final var creationDate2 = Instant.parse("2020-07-23T13:12:55Z");

    var encounter1Audit1 = AuditSession.builder()
        .createdDate(creationDate1)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "1")
        .build();
    var encounter1Audit2 = AuditSession.builder()
        .createdDate(creationDate2)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "1")
        .build();
    var encounter2Audit = AuditSession.builder()
        .createdDate(creationDate2)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "2")
        .build();
    var serviceSearch1Audit = AuditSession.builder()
        .createdDate(creationDate1)
        .additionalProperty("operation", "service_search")
        .additionalProperty("interactionId", "1")
        .build();

    var auditSessions = List.of(
        encounter1Audit1,
        encounter1Audit2,
        encounter2Audit,
        serviceSearch1Audit);

    var interactionGroups = auditTransformer.groupAndTransformInteractions(auditSessions);

    var expectedInteractionGroups = new Object[] {
        new AuditInteraction(OperationType.ENCOUNTER, "1", creationDate1.toString()),
        new AuditInteraction(OperationType.ENCOUNTER, "2", creationDate2.toString()),
        new AuditInteraction(OperationType.SERVICE_SEARCH, "1", creationDate1.toString())
    };

    assertThat(interactionGroups, containsInAnyOrder(expectedInteractionGroups));
  }

  @Test
  public void groupInteractions_returnsGroupsWithOldestDate() {
    final var creationDate1 = Instant.parse("2019-08-22T12:11:54Z");
    final var creationDate2 = Instant.parse("2020-07-23T13:12:55Z");

    var oldAudit = AuditSession.builder()
        .createdDate(creationDate1)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "1")
        .build();
    var newAudit = AuditSession.builder()
        .createdDate(creationDate2)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "1")
        .build();

    var auditSessions = List.of(oldAudit, newAudit);

    var interactionGroups = auditTransformer.groupAndTransformInteractions(auditSessions);

    var expectedInteractionGroups = new Object[] {
        new AuditInteraction(OperationType.ENCOUNTER, "1", creationDate1.toString()),
    };

    assertThat(interactionGroups, containsInAnyOrder(expectedInteractionGroups));
  }

  @Test
  public void groupInteractions_returnsOrderedByMostRecentGroups() {
    final var creationDate1 = Instant.parse("2019-08-22T12:11:54Z");
    final var creationDate2 = Instant.parse("2020-07-23T13:12:55Z");
    final var creationDate3 = Instant.parse("2021-08-24T14:13:56Z");

    var audit1 = AuditSession.builder()
        .createdDate(creationDate1)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "1")
        .build();
    var audit2 = AuditSession.builder()
        .createdDate(creationDate2)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "2")
        .build();
    var audit3 = AuditSession.builder()
        .createdDate(creationDate3)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "3")
        .build();

    var auditSessions = List.of(audit2, audit1, audit3);

    var interactionGroups = auditTransformer.groupAndTransformInteractions(auditSessions);

    var expectedInteractionGroups = new AuditInteraction[] {
        new AuditInteraction(OperationType.ENCOUNTER, "3", creationDate3.toString()),
        new AuditInteraction(OperationType.ENCOUNTER, "2", creationDate2.toString()),
        new AuditInteraction(OperationType.ENCOUNTER, "1", creationDate1.toString()),
    };

    assertThat(interactionGroups, contains(expectedInteractionGroups));
  }

}