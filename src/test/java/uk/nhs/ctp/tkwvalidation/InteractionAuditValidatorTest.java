package uk.nhs.ctp.tkwvalidation;

import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.cactus.common.audit.model.AuditSession;

public class InteractionAuditValidatorTest {

  private final InteractionAuditValidator rule = new InteractionAuditValidator();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void validate_withInteractionsLackingInteractionId_shouldFail() {
    var audits = List.of(
        AuditSession.builder().build(),
        AuditSession.builder()
            .additionalProperty("operation", "validOperationType")
            .build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.validate(audits);
  }

  @Test
  public void validate_withInteractionLackingInteractionId_shouldFail() {
    var audits = List.of(AuditSession.builder()
        .additionalProperty("operation", "validOperationType")
        .build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.validate(audits);
  }

  @Test
  public void validate_withInteractionsLackingOperation_shouldFail() {
    var audits = List.of(
        AuditSession.builder().build(),
        AuditSession.builder()
            .additionalProperty("interactionId", "validInteractionId")
            .build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.validate(audits);
  }

  @Test
  public void validate_withInteractionLackingOperation_shouldFail() {
    var audits = List.of(AuditSession.builder()
        .additionalProperty("interactionId", "validInteractionId")
        .build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.validate(audits);
  }

  @Test
  public void validate_withInteractions_shouldPass() {
    var audits = List.of(
        AuditSession.builder()
            .additionalProperty("operation", "validOperationType")
            .additionalProperty("interactionId", "validInteractionId2")
            .build(),
        AuditSession.builder()
            .additionalProperty("operation", "validOperationType")
            .additionalProperty("interactionId", "validInteractionId1")
            .build());

    rule.validate(audits);
  }

  @Test
  public void validate_withInteraction_shouldPass() {
    var audits = List.of(
        AuditSession.builder()
            .additionalProperty("operation", "validOperationType")
            .additionalProperty("interactionId", "validInteractionId2")
            .build());

    rule.validate(audits);
  }

}