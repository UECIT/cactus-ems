package uk.nhs.ctp.tkwvalidation;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.tkwvalidation.model.AuditMetadata;

@RunWith(MockitoJUnitRunner.class)
public class AuditMetadataCollectorTest {
  private static final Instant CREATED_AT_1 = Instant.parse("2019-06-05T09:12:20Z");
  private static final Instant CREATED_AT_2 = Instant.parse("2020-07-06T10:23:31Z");

  @Mock
  private CdssSupplierService cdssSupplierService;

  @InjectMocks
  private AuditMetadataCollector metadataCollector;

  @Test
  public void collect_withNoAudits_shouldReturnBasicMetadata() {
    var actualMetadata = metadataCollector.collect(
        Collections.emptyList(),
        OperationType.ENCOUNTER,
        "validEndpoint");

    var expectedMetadata = AuditMetadata.builder()
        .interactionType(OperationType.ENCOUNTER)
        .serviceEndpoint("validEndpoint")
        .build();

    assertThat(actualMetadata, sameBeanAs(expectedMetadata));
  }

  @Test
  public void collect_withAuditsAndNonCdssEndpoint_shouldReturnFullMetadata() {
    var audits = List.of(
        AuditSession.builder()
            .createdDate(CREATED_AT_2)
            .additionalProperty("supplierId", "validSupplierId")
            .additionalProperty("interactionId", "validCaseId")
            .build(),
        AuditSession.builder()
            .createdDate(CREATED_AT_1)
            .additionalProperty("supplierId", "validSupplierId")
            .additionalProperty("interactionId", "validCaseId")
            .build());

    when(cdssSupplierService.findCdssSupplierByBaseUrl("validEndpoint"))
        .thenReturn(Optional.empty());

    var actualMetadata = metadataCollector.collect(
        audits,
        OperationType.ENCOUNTER,
        "validEndpoint");

    var expectedMetadata = AuditMetadata.builder()
        .interactionType(OperationType.ENCOUNTER)
        .serviceEndpoint("validEndpoint")
        .supplierId("validSupplierId")
        .interactionId("validCaseId")
        .apiVersion(CdsApiVersion.TWO)
        .interactionDate(CREATED_AT_1)
        .build();

    assertThat(actualMetadata, sameBeanAs(expectedMetadata));
  }

  @Test
  public void collect_withCdssEndpoint_shouldReturnCdssApiVersion() {
    var audits = List.of(
        AuditSession.builder()
            .createdDate(CREATED_AT_2)
            .additionalProperty("supplierId", "validSupplierId")
            .additionalProperty("interactionId", "validCaseId")
            .build());

    var cdss = new CdssSupplier();
    cdss.setBaseUrl("validEndpoint");
    cdss.setSupportedVersion(CdsApiVersion.ONE_ONE);

    when(cdssSupplierService.findCdssSupplierByBaseUrl("validEndpoint"))
        .thenReturn(Optional.of(cdss));

    var actualMetadata = metadataCollector.collect(
        audits,
        OperationType.ENCOUNTER,
        "validEndpoint");

    var expectedMetadata = AuditMetadata.builder()
        .interactionType(OperationType.ENCOUNTER)
        .serviceEndpoint("validEndpoint")
        .supplierId("validSupplierId")
        .interactionId("validCaseId")
        .apiVersion(CdsApiVersion.ONE_ONE)
        .interactionDate(CREATED_AT_2)
        .build();

    assertThat(actualMetadata, sameBeanAs(expectedMetadata));
  }
}