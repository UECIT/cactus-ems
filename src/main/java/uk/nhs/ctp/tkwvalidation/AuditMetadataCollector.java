package uk.nhs.ctp.tkwvalidation;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

@Component
@Slf4j
public class AuditMetadataCollector {
  private static final String SUPPLIER_ID = "supplierId";
  private static final String CASE_ID = "caseId";

  public AuditMetadata collect(
      List<AuditSession> audits,
      OperationType operationType,
      String selectedServiceEndpoint) {
    var metadataBuilder = AuditMetadata.builder()
        .interactionType(operationType)
        .serviceEndpoint(selectedServiceEndpoint);
    if (audits.isEmpty()) {
      log.warn("Cannot infer audit metadata from 0 audits");
      return metadataBuilder.build();
    }

    metadataBuilder.supplierId(getSingleProperty(audits, SUPPLIER_ID));
    // TODO: get api version

    switch (operationType) {
      case ENCOUNTER:
        metadataBuilder.interactionId(getSingleProperty(audits, CASE_ID));
        break;
      case SERVICE_SEARCH:
        // TODO CDSCT-400: unify interaction id
        break;
    }

    var earliestDate = audits.stream()
        .min(comparing(AuditSession::getCreatedDate))
        .orElseThrow()
        .getCreatedDate();
    metadataBuilder.interactionDate(earliestDate);

    return metadataBuilder.build();
  }

  private String getSingleProperty(List<AuditSession> audits, String propertyName) {
    if (!audits.stream().allMatch(a -> a.getAdditionalProperties().containsKey(propertyName))) {
      log.warn("Not all received audits have a " + propertyName);
    }

    var values = audits.stream()
        .map(audit -> audit.getAdditionalProperties().get(propertyName))
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableList());

    var value = values.get(0);

    if (!values.stream().allMatch(value::equals)) {
      log.warn("Received audits have different " + propertyName);
    }

    return value;
  }
}
