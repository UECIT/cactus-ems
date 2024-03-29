package uk.nhs.ctp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.logging.LogContext;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.fhir.ResponseResolver;

@Service
@Slf4j
@AllArgsConstructor
public class EvaluateService {

  private final CdssSupplierService cdssSupplierService;
  private final EvaluateParametersService evaluateParametersService;
  private final CdssService cdssService;
  private final CaseService caseService;

  private final ResponseResolver responseResolver;

  /**
   * Executes ServiceDefinition $evaluate operation
   *
   * @param requestDetails {@link CdssRequestDTO}
   * @return {@link CdssResult}
   * @throws JsonProcessingException
   */
  public CdssResult evaluate(CdssRequestDTO requestDetails)
      throws Exception {
    CdssSupplier cdssSupplier = cdssSupplierService
        .getCdssSupplier(requestDetails.getCdssSupplierId());

    String requestId = UUID.randomUUID().toString();

    LogContext context = LogContext.builder()
        .task(LogContext.EVALUATE)
        .cds(cdssSupplier.getName())
        .encounter(requestDetails.getCaseId().toString())
        .request(requestId)
        .build();

    return context.wrap(() ->
        evaluateServiceDefinition(requestDetails, cdssSupplier, requestId)
    );
  }

  private CdssResult evaluateServiceDefinition(CdssRequestDTO requestDetails,
      CdssSupplier cdssSupplier, String requestId) {

    var caseId = requestDetails.getCaseId();

    // Fetch for audit
    cdssService.getServiceDefinition(
        requestDetails.getCdssSupplierId(),
        requestDetails.getServiceDefinitionId()
    );

    Parameters request =
        evaluateParametersService.getEvaluateParameters(requestDetails, cdssSupplier, requestId);

    GuidanceResponse response = cdssService.evaluateServiceDefinition(
        request,
        requestDetails.getCdssSupplierId(),
        requestDetails.getServiceDefinitionId()
    );

    CdssResult result = responseResolver
        .resolve(response, cdssSupplier, requestDetails.getSettings(),
            requestDetails.getPatientId());

    if (result.hasOutputData()) {
      caseService.updateCase(caseId, result);
    } else {
      log.warn("Result has no outputData");
    }

    result.setApiVersion(cdssSupplier.getSupportedVersion());
    return result;
  }

}
