package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.cactus.common.audit.model.AuditSession;

public class EncounterAuditValidationRuleTest {

  private final EncounterAuditValidationRule rule = new EncounterAuditValidationRule();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void ensure_withEncountersLackingCaseId_shouldFail() {
    var audits = List.of(
        AuditSession.builder().build(),
        AuditSession.builder()
            .additionalProperty("interactionId", "validCaseId")
            .build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.ensure(audits);
  }

  @Test
  public void ensure_withEncounterLackingCaseId_shouldFail() {
    var audits = List.of(AuditSession.builder().build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.ensure(audits);
  }

  @Test
  public void ensure_withEncounters_shouldPass() {
    var audits = List.of(
        AuditSession.builder()
            .additionalProperty("interactionId", "validCaseId2")
            .build(),
        AuditSession.builder()
            .additionalProperty("interactionId", "validCaseId1")
            .build());

    rule.ensure(audits);
  }

  @Test
  public void ensure_withEncounter_shouldPass() {
    var audits = List.of(
        AuditSession.builder()
            .additionalProperty("interactionId", "validCaseId2")
            .build());

    rule.ensure(audits);
  }

}