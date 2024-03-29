package uk.nhs.ctp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.EncounterReportInput;
import uk.nhs.ctp.service.dto.PractitionerDTO;
import uk.nhs.ctp.service.dto.TriageLaunchDTO;
import uk.nhs.ctp.service.dto.TriageQuestion;

@Service
@RequiredArgsConstructor
@Slf4j
public class TriageService {

  private final CaseService caseService;
  private final CdssService cdssService;
  private final ResponseService responseService;
  private final EncounterService encounterService;
  private final EvaluateService evaluateService;
  private final CompositionService compositionService;
  private final CarePlanService carePlanService;

  /**
   * Creates case from test case scenario and patient details and launches first triage request
   *
   * @param requestDetails {@link TriageLaunchDTO}
   * @return response {@link CdssResponseDTO}
   */
  public Long launchTriage(TriageLaunchDTO requestDetails) {
    String patientId = requestDetails.getPatientId();
    PractitionerDTO practitioner = requestDetails.getSettings().getPractitioner();
    Cases caseEntity = caseService.createCase(patientId, practitioner);
    Long caseId = caseEntity.getId();
    caseService.setupCaseDetails(caseEntity, patientId);

    String encounterId = requestDetails.getEncounterId();
    if (encounterId != null) {
      log.info("Continuing triage journey for encounter {}", encounterId);
      updateCaseFromEncounterReport(caseId, encounterId);
    }

    // Fetch for audit
    cdssService.getServiceDefinition(
        requestDetails.getCdssSupplierId(), requestDetails.getServiceDefinitionId());

    return caseId;
  }

  private void updateCaseFromEncounterReport(Long caseId, String encounterId) {
    //TODO: Ideally we cached the ER somewhere, for now we fetch it again
    EncounterReportInput encounterReportInput = encounterService
        .getEncounterReport(new IdType(encounterId));
    encounterReportInput.getObservations()
        .forEach(obs -> caseService.addResourceToCaseInputData(caseId, obs));
  }

  /**
   * Processes triage request and returns a summary of the response
   *
   * @param requestDetails {@link CdssRequestDTO}
   * @return response {@link CdssResponseDTO}
   */
  public CdssResponseDTO progressTriage(CdssRequestDTO requestDetails) throws Exception {

    Long caseId = requestDetails.getCaseId();
    if (requestDetails.getCarePlanIds() != null) {
      carePlanService.completeCarePlans(requestDetails.getCarePlanIds());
    }

    CdssResult cdssResult = evaluateService.evaluate(requestDetails);

    CdssResponseDTO cdssResponse = buildResponseDtoFromResult(
        cdssResult,
        caseId,
        requestDetails.getCdssSupplierId());

    compositionService.crupdate(caseId, cdssResult);

    return cdssResponse;
  }

  /**
   * Processes triage amend request and returns a summary of the response
   *
   * @param requestDetails {@link CdssRequestDTO}
   * @return response {@link CdssResponseDTO}
   */
  public CdssResponseDTO amendTriage(CdssRequestDTO requestDetails)
      throws Exception {

    Long caseId = requestDetails.getCaseId();

    CdssResult cdssResult = evaluateService.evaluate(requestDetails);

    return buildAmendResponseDtoFromResult(
        cdssResult, caseId, requestDetails.getCdssSupplierId(),
        requestDetails.getQuestionResponse());
  }

  /**
   * Builds a responseDTO from a cdssResult
   *
   * @param cdssResult {@link CdssResult}
   * @param caseId     {@link Long}
   * @return {@link CdssRequestDTO}
   */
  protected CdssResponseDTO buildResponseDtoFromResult(
      CdssResult cdssResult, Long caseId, Long cdssSupplierId)
      throws FHIRException {
    if (cdssResult == null) {
      throw new NullPointerException("CdssResult is empty");
    }
    Questionnaire questionnaire = null;
    if (!cdssResult.hasResult() && cdssResult.hasQuestionnaire()) {
      questionnaire = cdssService
          .getQuestionnaire(cdssSupplierId, cdssResult.getQuestionnaireRef());
    }
    return responseService.buildResponse(cdssResult, questionnaire, caseId, cdssSupplierId);
  }

  /**
   * Builds an amended responseDTO from a cdssResult
   *
   * @param cdssResult        {@link CdssResult}
   * @param caseId            {@link Long}
   * @return {@link CdssRequestDTO}
   */
  protected CdssResponseDTO buildAmendResponseDtoFromResult(
      CdssResult cdssResult, Long caseId,
      Long cdssSupplierId, TriageQuestion[] previousQuestions)
      throws FHIRException {
    if (cdssResult == null) {
      throw new NullPointerException("CdssResult is empty");
    }
    Questionnaire questionnaire = null;

    if (!cdssResult.hasResult() && cdssResult.hasQuestionnaire()) {
      questionnaire = cdssService.getQuestionnaire(cdssSupplierId,
          "Questionnaire/" + previousQuestions[0].getQuestionnaireId());
    }
    return responseService
        .buildAmendResponse(cdssResult, questionnaire, caseId, cdssSupplierId, previousQuestions);
  }
}
