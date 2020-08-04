package uk.nhs.ctp.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.transform.CaseObservationTransformer;

@RunWith(MockitoJUnitRunner.class)
public class TriageServiceTest {

  @InjectMocks
  private TriageService triageService;

  @Mock
  private CaseService caseService;

  @Mock
  private CdssService cdssService;

  @Mock
  private ResponseService responseService;

  @Mock
  private EncounterService encounterService;

  @Mock
  private CaseObservationTransformer caseObservationTransformer;

  @Mock
  private CompositionService compositionService;

  @Mock
  private EvaluateService evaluateService;

  @Mock
  private CarePlanService carePlanService;

  @Test(expected = NullPointerException.class)
  public void testExceptionThrownWhenCdssResultIsNull() throws FHIRException {
    triageService.buildResponseDtoFromResult(null, 1L, 1L);
  }

  @Test
  public void testQuestionnaireRequestMadeWhenDataRequirementPresent() throws FHIRException {
    var cdssResult = new CdssResult();
    cdssResult.setQuestionnaireRef("Questionnaire/1");
    when(cdssService.getQuestionnaire(1L, "Questionnaire/1"))
        .thenReturn(new Questionnaire());

    triageService.buildResponseDtoFromResult(cdssResult, 1L, 1L);

    verify(cdssService).getQuestionnaire(1L, "Questionnaire/1");
  }

  @Test
  public void testQuestionnaireRequestNotMadeWhenResultAndDataRequirementPresent()
      throws FHIRException {
    var cdssResult = new CdssResult();
    cdssResult.setResult(new RequestGroup());
    cdssResult.setQuestionnaireRef("Questionnaire/1");

    triageService.buildResponseDtoFromResult(cdssResult, 1L, 1L);

    verify(cdssService, never()).getQuestionnaire(anyLong(), anyString());
  }

  @Test
  public void testQuestionnaireRequestNotMadeWhenOnlyResultIsPresent() throws FHIRException {
    var cdssResult = new CdssResult();
    cdssResult.setResult(new RequestGroup());

    triageService.buildResponseDtoFromResult(cdssResult, 1L, 1L);

    verify(cdssService, never()).getQuestionnaire(anyLong(), anyString());
  }

  @Test
  public void progressTriageRequest_withCarePlanIds_willCompleteCarePlans() throws Exception {
    var requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setCarePlanIds(new String[]{"id1", "id2"});
    when(evaluateService.evaluate(requestDTO)).thenReturn(new CdssResult());

    triageService.progressTriage(requestDTO);

    verify(carePlanService).completeCarePlans(requestDTO.getCarePlanIds());

  }

  @Test
  public void progressTriageRequest_withNullCarePlanIds_willNotCompleteCarePlans() throws Exception {
    var requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    when(evaluateService.evaluate(requestDTO)).thenReturn(new CdssResult());

    triageService.progressTriage(requestDTO);

    verify(carePlanService, never()).completeCarePlans(any());
  }
}
