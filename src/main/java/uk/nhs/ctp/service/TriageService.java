package uk.nhs.ctp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.ConnectException;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.exceptions.FHIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.TriageLaunchDTO;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.service.encounter.EncounterService;
import uk.nhs.ctp.service.factory.ReferencingContextFactory;
import uk.nhs.ctp.service.resolver.ResponseResolver;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Service
@AllArgsConstructor
public class TriageService {

  private static final Logger LOG = LoggerFactory.getLogger(TriageService.class);

  private CaseService caseService;
  private CdssService cdssService;
  private ParametersService parametersService;
  private ResponseService responseService;
  private AuditService auditService;
  private CdssSupplierService cdssSupplierService;
  private ReferencingContextFactory referencingContextFactory;
  private ResponseResolver responseResolver;
  private EncounterService encounterService;

  /**
   * Creates case from test case scenario and patient details and launches first triage request
   *
   * @param requestDetails {@link TriageLaunchDTO}
   * @return response {@link CdssResponseDTO}
   * @throws JsonProcessingException
   */
  public CdssResponseDTO launchTriage(TriageLaunchDTO requestDetails)
      throws ConnectException, JsonProcessingException, FHIRException {

    Cases triageCase = caseService.createCase(requestDetails.getPatientId());

    CdssRequestDTO cdssRequest = new CdssRequestDTO();
    cdssRequest.setCaseId(triageCase.getId());
    cdssRequest.setCdssSupplierId(requestDetails.getCdssSupplierId());
    cdssRequest.setServiceDefinitionId(requestDetails.getServiceDefinitionId());
    cdssRequest.setSettings(requestDetails.getSettings());
    cdssRequest.setPatientId(requestDetails.getPatientId());

    return processTriageRequest(cdssRequest);
  }

  /**
   * Processes triage request and returns a summary of the response
   *
   * @param requestDetails {@link CdssRequestDTO}
   * @return response {@link CdssResponseDTO}
   * @throws JsonProcessingException
   */
  public CdssResponseDTO processTriageRequest(CdssRequestDTO requestDetails)
      throws ConnectException, JsonProcessingException, FHIRException {

    Long caseId = requestDetails.getCaseId();
    LOG.info("Processing triage for case " + caseId);

    // start audit
    AuditRecord auditRecord = auditService.createNewAudit(caseId);

    CdssResult cdssResult = updateCaseUsingCdss(requestDetails);

    CdssResponseDTO cdssResponse = buildResponseDtoFromResult(cdssResult,
        caseId,
        requestDetails.getCdssSupplierId());

    auditService
        .updateAuditEntry(auditRecord, requestDetails, cdssResponse, cdssResult.getContained());

    if (cdssResult.isInProgress()) {
      requestDetails.setQuestionResponse(null);
      cdssResult = updateCaseUsingCdss(requestDetails);

      // Add Audit Record
      cdssResponse = buildResponseDtoFromResult(cdssResult, caseId,
          requestDetails.getCdssSupplierId());
      auditService
          .updateAuditEntry(auditRecord, requestDetails, cdssResponse, cdssResult.getContained());
    }

    return cdssResponse;
  }

  /**
   * Processes triage amend request and returns a summary of the response
   *
   * @param requestDetails {@link CdssRequestDTO}
   * @return response {@link CdssResponseDTO}
   * @throws JsonProcessingException
   */
  public CdssResponseDTO processTriageAmendRequest(CdssRequestDTO requestDetails)
      throws ConnectException, JsonProcessingException, FHIRException {

    Long caseId = requestDetails.getCaseId();
    LOG.info("Amending triage for case " + caseId);

    // start audit
    AuditRecord auditRecord = auditService.createNewAudit(caseId);
    CdssResult cdssResult = amendCaseUsingCdss(requestDetails);

    // Add Audit Record
    CdssResponseDTO cdssResponse = buildAmendResponseDtoFromResult(
        cdssResult, caseId, requestDetails.getCdssSupplierId(),
        requestDetails.getQuestionResponse());
    auditService
        .updateAuditEntry(auditRecord, requestDetails, cdssResponse, cdssResult.getContained());

    return cdssResponse;
  }

  /**
   * Executes ServiceDefinition $evaluate operation and stores any output data in the DB
   *
   * @param requestDetails {@link CdssRequestDTO}
   * @return {@link CdssResult}
   * @throws JsonProcessingException
   */
  protected CdssResult updateCaseUsingCdss(CdssRequestDTO requestDetails)
      throws ConnectException, JsonProcessingException {

    CdssResult cdssResult = amendCaseUsingCdss(requestDetails);

    if (cdssResult.hasOutputData() || cdssResult.getSessionId() != null) {
      Long caseId = requestDetails.getCaseId();
      LOG.info("Update case for " + caseId);
      caseService.updateCase(caseId, cdssResult.getOutputData(),
          cdssResult.getSessionId());
    }

    return cdssResult;
  }

  /**
   * Executes ServiceDefinition $evaluate operation and stores any output data in the DB
   *
   * @param requestDetails {@link CdssRequestDTO}
   * @return {@link CdssResult}
   * @throws JsonProcessingException
   */
  protected CdssResult amendCaseUsingCdss(CdssRequestDTO requestDetails)
      throws ConnectException, JsonProcessingException {
    var referencingContext = referencingContextFactory.load(requestDetails.getCdssSupplierId());

    Parameters parameters = parametersService.getEvaluateParameters(
        requestDetails.getCaseId(),
        requestDetails.getQuestionResponse(),
        requestDetails.getSettings(),
        requestDetails.getAmendingPrevious(),
        referencingContext,
        requestDetails.getQuestionnaireId());

    GuidanceResponse resource = cdssService.evaluateServiceDefinition(
        parameters,
        requestDetails.getCdssSupplierId(),
        requestDetails.getServiceDefinitionId(),
        requestDetails.getCaseId(),
        referencingContext);

    CdssSupplier cdssSupplier = cdssSupplierService
        .getCdssSupplier(requestDetails.getCdssSupplierId());

    return responseResolver.resolve(resource, cdssSupplier, requestDetails.getSettings(),
        requestDetails.getPatientId());
  }

  /**
   * Builds a responseDTO from a cdssResult
   *
   * @param cdssResult {@link CdssResult}
   * @param caseId     {@link Long}
   * @return {@link CdssRequestDTO}
   * @throws JsonProcessingException
   */
  protected CdssResponseDTO buildResponseDtoFromResult(CdssResult cdssResult, Long caseId,
      Long cdssSupplierId)
      throws ConnectException, JsonProcessingException, FHIRException {
    if (cdssResult == null) {
      throw new NullPointerException("CdssResult is empty");
    }
    Questionnaire questionnaire = null;
    if (!cdssResult.hasResult() && cdssResult.hasQuestionnaire()) {
      // TODO GetResouce out of contained
      questionnaire = ResourceProviderUtils
          .getResource(cdssResult.getContained(), Questionnaire.class);
      questionnaire = questionnaire == null ? cdssService
          .getQuestionnaire(cdssSupplierId, cdssResult.getQuestionnaireRef(), caseId)
          : questionnaire;
    }
    return responseService.buildResponse(cdssResult, questionnaire, caseId, cdssSupplierId);
  }

  /**
   * Builds an amended responseDTO from a cdssResult
   *
   * @param cdssResult        {@link CdssResult}
   * @param caseId            {@link Long}
   * @param previousQuestions
   * @return {@link CdssRequestDTO}
   * @throws JsonProcessingException
   */
  protected CdssResponseDTO buildAmendResponseDtoFromResult(CdssResult cdssResult, Long caseId,
      Long cdssSupplierId,
      TriageQuestion[] previousQuestions)
      throws ConnectException, JsonProcessingException, FHIRException {
    if (cdssResult == null) {
      throw new NullPointerException("CdssResult is empty");
    }
    Questionnaire questionnaire = null;

    if (!cdssResult.hasResult() && cdssResult.hasQuestionnaire()) {
      questionnaire = cdssService.getQuestionnaire(cdssSupplierId,
          "Questionnaire/" + previousQuestions[0].getQuestionnaireId(), caseId);
    }
    return responseService
        .buildAmendResponse(cdssResult, questionnaire, caseId, cdssSupplierId, previousQuestions);
  }
}
