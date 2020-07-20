package uk.nhs.ctp.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
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

  CdssResult mockCdssResult;
  CdssResponseDTO mockCdssResponseDTO;
  CdssRequestDTO mockCdssRequestDTO;
  Questionnaire mockQuestionnaire;

  @Before
  public void setup() throws Exception {
    mockCdssResult = mock(CdssResult.class);
    mockCdssResponseDTO = mock(CdssResponseDTO.class);
    mockCdssRequestDTO = mock(CdssRequestDTO.class);
    when(mockCdssRequestDTO.getCaseId()).thenReturn(1L);
    when(mockCdssRequestDTO.getCdssSupplierId()).thenReturn(1L);

    when(evaluateService.evaluate(mockCdssRequestDTO)).thenReturn(mockCdssResult);

    mockQuestionnaire = mock(Questionnaire.class);

  }

  @Test(expected = NullPointerException.class)
  public void testExceptionThrownWhenCdssResultIsNull() throws FHIRException {
    triageService.buildResponseDtoFromResult(null, 1L, 1L);
  }

  @Test
  public void testQuestionnaireRequestMadeWhenDataRequirementPresent() throws FHIRException {
    when(mockCdssResult.hasResult())
        .thenReturn(false);
    when(mockCdssResult.hasQuestionnaire())
        .thenReturn(true);
    when(mockCdssResult.getQuestionnaireRef())
        .thenReturn("Questionnaire/1");
    when(cdssService.getQuestionnaire(1L, "Questionnaire/1"))
        .thenReturn(mockQuestionnaire);
    when(responseService.buildResponse(mockCdssResult, mockQuestionnaire, 1L, 1L))
        .thenReturn(mockCdssResponseDTO);

    triageService.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

    verify(cdssService, times(1)).getQuestionnaire(1L, "Questionnaire/1");
  }

  @Test
  public void testQuestionnaireRequestNotMadeWhenResultAndDataRequirementPresent()
      throws FHIRException {
    when(mockCdssResult.hasResult())
        .thenReturn(true);
    when(mockCdssResult.hasQuestionnaire())
        .thenReturn(true);
    when(responseService.buildResponse(mockCdssResult, null, 1L, 1L))
        .thenReturn(mockCdssResponseDTO);

    triageService.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

    verify(cdssService, times(0)).getQuestionnaire(anyLong(), anyString());
  }

  @Test
  public void testQuestionnaireRequestNotMadeWhenOnlyResultIsPresent() throws FHIRException {
    when(mockCdssResult.hasResult())
        .thenReturn(true);
    when(mockCdssResult.hasQuestionnaire())
        .thenReturn(false);
    when(responseService.buildResponse(mockCdssResult, null, 1L, 1L))
        .thenReturn(mockCdssResponseDTO);

    triageService.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

    verify(cdssService, times(0)).getQuestionnaire(anyLong(), anyString());
  }

  @Test
  public void processTriageRequest_withCarePlanIds_willCompleteCarePlans() throws Exception {
    var carePlanIds = new String[]{"id1", "id2"};
    when(mockCdssRequestDTO.getCarePlanIds()).thenReturn(carePlanIds);

    when(mockCdssResult.hasResult()).thenReturn(true);
    when(mockCdssResult.hasQuestionnaire()).thenReturn(false);
    when(responseService.buildResponse(mockCdssResult, null, 1L, 1L))
        .thenReturn(mockCdssResponseDTO);

    triageService.processTriageRequest(mockCdssRequestDTO);

    verify(carePlanService).completeCarePlans(carePlanIds);

  }

  @Test
  public void processTriageRequest_withNullCarePlanIds_willNotCompleteCarePlans() throws Exception {
    when(mockCdssRequestDTO.getCarePlanIds()).thenReturn(null);

    when(mockCdssResult.hasResult()).thenReturn(true);
    when(mockCdssResult.hasQuestionnaire()).thenReturn(false);
    when(responseService.buildResponse(mockCdssResult, null, 1L, 1L))
        .thenReturn(mockCdssResponseDTO);

    triageService.processTriageRequest(mockCdssRequestDTO);

    verify(carePlanService, times(0)).completeCarePlans(any());
  }

}
