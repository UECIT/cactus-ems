package uk.nhs.ctp.service;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.ConnectException;
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
import uk.nhs.ctp.service.factory.ReferencingContextFactory;
import uk.nhs.ctp.service.resolver.ResponseResolver;

@RunWith(MockitoJUnitRunner.class)
public class TriageServiceTest {

	private TriageService triageService;
	private TriageService spyTriageService;

	@Mock
	private CaseService caseService;

	@Mock
	private CdssService cdssService;

	@Mock
	private AuditService auditService;

	@Mock
	private ParametersService parametersService;

	@Mock
	private CdssSupplierService cdssSupplierService;

	@Mock
	private ResponseService responseService;

	@Mock
	private ReferencingContextFactory referencingContextFactory;

	@Mock
	private ResponseResolver responseResolver;

	CdssResult mockCdssResult;
	CdssResponseDTO mockCdssResponseDTO;
	CdssRequestDTO mockCdssRequestDTO;
	Questionnaire mockQuestionnaire;

	@Before
	public void setup() {
		spyTriageService = spy(new TriageService(
				caseService,
				cdssService,
				parametersService,
				responseService,
				auditService,
				cdssSupplierService,
				referencingContextFactory,
				responseResolver
		));

		triageService = new TriageService(
				caseService,
				cdssService,
				parametersService,
				responseService,
				auditService,
				cdssSupplierService,
				referencingContextFactory,
				responseResolver
		);

		mockCdssResult = mock(CdssResult.class);
		mockCdssResponseDTO = mock(CdssResponseDTO.class);
		mockCdssRequestDTO = mock(CdssRequestDTO.class);
		when(mockCdssRequestDTO.getCaseId()).thenReturn(1L);
		when(mockCdssRequestDTO.getCdssSupplierId()).thenReturn(1L);

		mockQuestionnaire = mock(Questionnaire.class);

	}

	@Test
	public void testSecondRequestMadeTwiceWhenNoResultOrDataRequirementReturned() throws ConnectException, JsonProcessingException, FHIRException {
		doReturn(mockCdssResult)
			.when(spyTriageService)
			.updateCaseUsingCdss(mockCdssRequestDTO);
		when(mockCdssResult.isInProgress())
			.thenReturn(true)
			.thenReturn(false);
		doReturn(mockCdssResponseDTO)
			.when(spyTriageService)
			.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

		spyTriageService.processTriageRequest(mockCdssRequestDTO);

		verify(spyTriageService, times(2)).updateCaseUsingCdss(mockCdssRequestDTO);

	}

	@Test
	public void testNoSecondRequestMadeOnceWhenResultInProgress() throws ConnectException, JsonProcessingException, FHIRException {
		doReturn(mockCdssResult)
			.when(spyTriageService)
			.updateCaseUsingCdss(mockCdssRequestDTO);
		when(mockCdssResult.isInProgress())
			.thenReturn(false);
		doReturn(mockCdssResponseDTO)
			.when(spyTriageService)
			.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

		spyTriageService.processTriageRequest(mockCdssRequestDTO);

		verify(spyTriageService, times(1)).updateCaseUsingCdss(mockCdssRequestDTO);

	}

	@Test(expected = NullPointerException.class)
	public void testExceptionThrownWhenCdssResultIsNull() throws ConnectException, JsonProcessingException, FHIRException {
		triageService.buildResponseDtoFromResult(null, 1L, 1L);
	}

	@Test
	public void testQuestionnaireRequestMadeWhenDataRequirementPresent() throws ConnectException, JsonProcessingException, FHIRException {
		when(mockCdssResult.hasResult())
			.thenReturn(false);
		when(mockCdssResult.hasQuestionnaire())
			.thenReturn(true);
		when(mockCdssResult.getQuestionnaireRef())
			.thenReturn("Questionnaire/1");
		when(cdssService.getQuestionnaire(1L, "Questionnaire/1", 1L))
			.thenReturn(mockQuestionnaire);
		when(responseService.buildResponse(mockCdssResult, mockQuestionnaire, 1L, 1L))
			.thenReturn(mockCdssResponseDTO);

		triageService.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

		verify(cdssService, times(1)).getQuestionnaire(1L, "Questionnaire/1", 1L);
	}

	@Test
	public void testQuestionnaireRequestNotMadeWhenResultAndDataRequirementPresent() throws ConnectException, JsonProcessingException, FHIRException {
		when(mockCdssResult.hasResult())
			.thenReturn(true);
		when(mockCdssResult.hasQuestionnaire())
			.thenReturn(true);
		when(responseService.buildResponse(mockCdssResult, null, 1L, 1L))
			.thenReturn(mockCdssResponseDTO);

		triageService.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

		verify(cdssService, times(0)).getQuestionnaire(anyLong(), anyString(), anyLong());
	}

	@Test
	public void testQuestionnaireRequestNotMadeWhenOnlyResultIsPresent() throws ConnectException, JsonProcessingException, FHIRException {
		when(mockCdssResult.hasResult())
			.thenReturn(true);
		when(mockCdssResult.hasQuestionnaire())
			.thenReturn(false);
		when(responseService.buildResponse(mockCdssResult, null, 1L, 1L))
			.thenReturn(mockCdssResponseDTO);

		triageService.buildResponseDtoFromResult(mockCdssResult, 1L, 1L);

		verify(cdssService, times(0)).getQuestionnaire(anyLong(), anyString(), anyLong());
	}

}
