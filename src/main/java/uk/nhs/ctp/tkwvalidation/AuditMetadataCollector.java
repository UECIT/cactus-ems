package uk.nhs.ctp.tkwvalidation;

import static java.util.Comparator.comparing;
import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;
import static uk.nhs.cactus.common.audit.model.AuditProperties.SUPPLIER_ID;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditMetadataCollector {

  private final CdssSupplierService cdssSupplierService;

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

    var apiVersion = cdssSupplierService.findCdssSupplierByBaseUrl(selectedServiceEndpoint)
        .map(CdssSupplier::getSupportedVersion)
        .orElse(CdsApiVersion.TWO);
    metadataBuilder.apiVersion(apiVersion);
    metadataBuilder.interactionId(getSingleProperty(audits, INTERACTION_ID));

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
