package uk.nhs.ctp.tkwvalidation.rules;

import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;

public class ServiceSearchAuditValidationRuleTest {
  private final ServiceSearchAuditValidationRule rule = new ServiceSearchAuditValidationRule();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void ensure_withMoreThan1ServiceSearchAudit_shouldFail() {
    var audits = List.of(
        AuditSession.builder().build(),
        AuditSession.builder().build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.ensure(audits);
  }

  @Test
  public void ensure_withMoreThan1ServiceSearchAuditEntry_shouldFail() {
    var audits = List.of(AuditSession.builder()
        .entry(AuditEntry.builder().build())
        .entry(AuditEntry.builder().build())
        .build());

    expectedException.expect(UnsupportedOperationException.class);
    rule.ensure(audits);
  }

  @Test
  public void ensure_with1ServiceSearchAuditEntry_shouldPass() {
    var audits = List.of(AuditSession.builder()
        .entry(AuditEntry.builder().build())
        .build());

    rule.ensure(audits);
  }
}