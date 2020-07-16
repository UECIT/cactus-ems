package uk.nhs.ctp.tkwvalidation;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;

@Component
@RequiredArgsConstructor
public class AuditSelector {
  private static final String CASE_ID = "caseId";

  private final FhirMessageAuditTransformer fhirMessageAuditTransformer;

  public List<FhirMessageAudit> selectAudits(
      Collection<AuditSession> audits,
      OperationType operationType) {
    var zippableAudits = new ArrayList<FhirMessageAudit>();

    for (var audit : audits) {
      var baseName = operationType == OperationType.SERVICE_SEARCH
          ? operationType.getName()
          : "encounter" + audit.getAdditionalProperties().get(CASE_ID);

      if (isMethod(audit.getRequestMethod(), POST)) {
        zippableAudits.add(fhirMessageAuditTransformer.from(audit, baseName, true));
      }
      if (isMethod(audit.getRequestMethod(), GET)) {
        zippableAudits.add(fhirMessageAuditTransformer.from(audit, baseName, false));
      }

      for (var entry : audit.getEntries()) {
        if (isMethod(entry.getRequestMethod(), POST)) {
          zippableAudits.add(fhirMessageAuditTransformer.from(entry, baseName, true));
        }
        if (isMethod(entry.getRequestMethod(), GET)) {
          zippableAudits.add(fhirMessageAuditTransformer.from(entry, baseName, false));
        }
      }
    }

    zippableAudits.sort(Comparator.comparing(FhirMessageAudit::getMoment));
    return zippableAudits;
  }

  private static boolean isMethod(String method, RequestMethod... expectedMethods) {
    return Stream.of(expectedMethods).map(Enum::name).anyMatch(method::equalsIgnoreCase);
  }

}
