package uk.nhs.ctp.service;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.transform.CaseObservationTransformer;

@RunWith(MockitoJUnitRunner.class)
public class TriageServiceTest {

  private TriageService triageService;
  private TriageService spyTriageService;

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
  private FhirContext fhirContext;

  CdssResult mockCdssResult;
  CdssResponseDTO mockCdssResponseDTO;
  CdssRequestDTO mockCdssRequestDTO;
  Questionnaire mockQuestionnaire;

  @Before
  public void setup() {
    spyTriageService = spy(new TriageService(
        caseService,
        cdssService,
        responseService,
        encounterService,
        evaluateService,
        caseObservationTransformer,
        compositionService
    ));

    triageService = new TriageService(
        caseService,
        cdssService,
        responseService,
        encounterService,
        evaluateService,
        caseObservationTransformer,
        compositionService
    );

    mockCdssResult = mock(CdssResult.class);
    mockCdssResponseDTO = mock(CdssResponseDTO.class);
    mockCdssRequestDTO = mock(CdssRequestDTO.class);
    when(mockCdssRequestDTO.getCaseId()).thenReturn(1L);
    when(mockCdssRequestDTO.getCdssSupplierId()).thenReturn(1L);

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

}
