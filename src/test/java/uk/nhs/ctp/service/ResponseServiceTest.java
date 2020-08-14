package uk.nhs.ctp.service;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestGroupActionComponent;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.ErrorMessageDTO;
import uk.nhs.ctp.service.dto.ReferralRequestDTO;
import uk.nhs.ctp.service.dto.TriageOption;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.transform.ErrorMessageTransformer;
import uk.nhs.ctp.transform.QuestionnaireOptionValueTransformer;
import uk.nhs.ctp.transform.ReferralRequestDTOTransformer;
import uk.nhs.ctp.transform.one_one.ReferralRequestDTOOneOneTransformer;
import uk.nhs.ctp.transform.two.ReferralRequestDTOTwoTransformer;
import uk.nhs.ctp.utils.ImplementationResolver;

@RunWith(MockitoJUnitRunner.class)
public class ResponseServiceTest {
	
	@InjectMocks
	private ResponseService responseService;

	@Mock
	private ErrorMessageTransformer errorMessageTransformer;
	@Mock
	private ImplementationResolver<ReferralRequestDTOTransformer> implementationResolver;
	@Mock
	private QuestionnaireOptionValueTransformer optionValueTransformer;
	@Mock
	private ReferralRequestDTOOneOneTransformer referralRequestVOneOneTransformer;
	@Mock
	private ReferralRequestDTOTwoTransformer referralRequestVTwoTransformer;

	@Test
	public void shouldReturnBaseResultWithError() {
		CdssResult error = new CdssResult();
		OperationOutcome operationOutcome = new OperationOutcome()
				.addIssue(new OperationOutcomeIssueComponent().setDiagnostics("Something wrong"));
		error.setOperationOutcome(operationOutcome);
		ErrorMessageDTO expectedError = ErrorMessageDTO.builder()
				.display("Something wrong")
				.build();
		when(errorMessageTransformer.transform(operationOutcome))
				.thenReturn(expectedError);

		CdssResponseDTO response = responseService.buildResponse(error, null, 4L, 6L);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setCaseId(4L);
		expected.setCdssSupplierId(6L);
		expected.setErrorMessage(expectedError);
		assertThat(response, sameBeanAs(expected));
		verifyZeroInteractions(implementationResolver, referralRequestVOneOneTransformer, referralRequestVTwoTransformer);
	}

	@Test
	//TODO: Original tests only used a care plan, once refactored we can test more and clean this up.
	public void shouldReturnResultWithRequestGroup() {
		CdssResult resultWithCarePlan = new CdssResult();
		IBaseReference carePlan = new Reference().setResource(
				new CarePlan()
						.addActivity(new CarePlanActivityComponent()
								.addOutcomeCodeableConcept(
										new CodeableConcept(
												new Coding("outcomeSys", "outcomeCode", "outcomeDisplay")))));
		resultWithCarePlan.setResult(new RequestGroup()
					.addAction(new RequestGroupActionComponent().setResource((Reference) carePlan)));

		CdssResponseDTO response = responseService
				.buildResponse(resultWithCarePlan, null, 5L, 1L);

		verifyZeroInteractions(
				implementationResolver,
				referralRequestVTwoTransformer,
				referralRequestVOneOneTransformer);
		verify(errorMessageTransformer).transform(null);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setResult("outcomeDisplay");
		expected.setCaseId(5L);
		expected.setCdssSupplierId(1L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	//TODO: tested method should split for more efficient testing
	public void shouldReturnResultWithQuestionnaire() {
		CdssResult emptyResult = new CdssResult();
		QuestionnaireItemOptionComponent option1 = new QuestionnaireItemOptionComponent()
				.setValue(new Coding("optionSys", "optionCode", "optionDisplay"));
		QuestionnaireItemOptionComponent option2 = new QuestionnaireItemOptionComponent()
				.setValue(new Coding("optionSys2", "optionCode2", "optionDisplay2"));
		Questionnaire questionnaire = new Questionnaire()
				.setStatus(PublicationStatus.ACTIVE)
				.addItem(new QuestionnaireItemComponent()
						.setLinkId("1")
						.setType(QuestionnaireItemType.CHOICE)
						.setText("Test question")
						.addOption(option1)
						.addOption(option2));
		questionnaire.setId("1");

		when(optionValueTransformer.transform(option1))
				.thenReturn(new TriageOption("sys", "optionCode", "optionDisplay"));
		when(optionValueTransformer.transform(option2))
				.thenReturn(new TriageOption("sys2", "optionCode2", "optionDisplay2"));

		CdssResponseDTO response = responseService
				.buildResponse(emptyResult, questionnaire, 5L, 1L);

		verifyZeroInteractions(
				implementationResolver,
				referralRequestVTwoTransformer,
				referralRequestVOneOneTransformer);
		verify(errorMessageTransformer).transform(null);
		CdssResponseDTO expected = new CdssResponseDTO();
		TriageQuestion expectedQuestion = new TriageQuestion();
		expectedQuestion.setQuestion("Test question");
		expectedQuestion.setQuestionnaireId("1");
		expectedQuestion.setQuestionId("1");
		expectedQuestion.setQuestionType("CHOICE");
		expectedQuestion.addOption("sys", "optionCode", "optionDisplay");
		expectedQuestion.addOption("sys2", "optionCode2", "optionDisplay2");
		expected.setTriageQuestions(Collections.singletonList(expectedQuestion));
		expected.setCaseId(5L);
		expected.setCdssSupplierId(1L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	//TODO: tested method should split for more efficient testing
	public void shouldReturnResultWithResultAndQuestionnaire() {
		CdssResult resultWithCarePlan = new CdssResult();
		Questionnaire questionnaire = new Questionnaire()
				.setStatus(PublicationStatus.ACTIVE)
				.addItem(new QuestionnaireItemComponent()
						.setLinkId("1")
						.setType(QuestionnaireItemType.CHOICE)
						.setText("Test question")
						.addOption(new QuestionnaireItemOptionComponent()
								.setValue(new Coding("optionSys", "optionCode", "optionDisplay")))
						.addOption(new QuestionnaireItemOptionComponent()
								.setValue(new Coding("optionSys2", "optionCode2", "optionDisplay2"))));
		questionnaire.setId("1");
		IBaseReference carePlan = new Reference().setResource(
				new CarePlan()
						.addActivity(new CarePlanActivityComponent()
								.addOutcomeCodeableConcept(
										new CodeableConcept(
												new Coding("outcomeSys", "outcomeCode", "outcomeDisplay")))));
		resultWithCarePlan.setResult(new RequestGroup()
				.addAction(new RequestGroupActionComponent().setResource((Reference) carePlan)));

		CdssResponseDTO response = responseService
				.buildResponse(resultWithCarePlan, questionnaire, 5L, 1L);

		verifyZeroInteractions(
				implementationResolver,
				referralRequestVTwoTransformer,
				referralRequestVOneOneTransformer);
		verify(errorMessageTransformer).transform(null);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setTriageQuestions(null);
		expected.setResult("outcomeDisplay");
		expected.setCaseId(5L);
		expected.setCdssSupplierId(1L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	public void shouldReturnResultWithSDSwitchTrigger() {
		CdssResult resultWithSwitch = new CdssResult();
		resultWithSwitch.setSwitchTrigger("trigger");

		CdssResponseDTO response = responseService.buildResponse(resultWithSwitch, null, 4L, 2L);

		verifyZeroInteractions(
				implementationResolver,
				referralRequestVTwoTransformer,
				referralRequestVOneOneTransformer);
		verify(errorMessageTransformer).transform(null);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setSwitchTrigger("trigger");
		expected.setCaseId(4L);
		expected.setCdssSupplierId(2L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	public void shouldReturnResultWithReferralRequestV1() {
		CdssResult resultWithReferral = new CdssResult();
		ReferralRequest referralRequest = new ReferralRequest().setDescription("Something");
		resultWithReferral.setReferralRequest(referralRequest);
		resultWithReferral.setApiVersion(CdsApiVersion.ONE_ONE);
		ReferralRequestDTO expectedReferral = ReferralRequestDTO.builder()
				.description("Something")
				.build();

		when(implementationResolver.resolve(CdsApiVersion.ONE_ONE))
				.thenReturn(referralRequestVOneOneTransformer);
		when(referralRequestVOneOneTransformer.transform(referralRequest))
				.thenReturn(expectedReferral);

		CdssResponseDTO response = responseService.buildResponse(resultWithReferral, null, 4L, 2L);

		verify(errorMessageTransformer).transform(null);
		verifyZeroInteractions(referralRequestVTwoTransformer);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setReferralRequest(expectedReferral);
		expected.setCaseId(4L);
		expected.setCdssSupplierId(2L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	public void shouldReturnResultWithReferralRequestV2() {
		CdssResult resultWithReferral = new CdssResult();
		ReferralRequest referralRequest = new ReferralRequest().setDescription("Something");
		resultWithReferral.setReferralRequest(referralRequest);
		resultWithReferral.setApiVersion(CdsApiVersion.TWO);
		ReferralRequestDTO expectedReferral = ReferralRequestDTO.builder()
				.description("Something")
				.build();

		when(implementationResolver.resolve(CdsApiVersion.TWO))
				.thenReturn(referralRequestVTwoTransformer);
		when(referralRequestVTwoTransformer.transform(referralRequest))
				.thenReturn(expectedReferral);

		CdssResponseDTO response = responseService.buildResponse(resultWithReferral, null, 4L, 2L);

		verify(errorMessageTransformer).transform(null);
		verifyZeroInteractions(referralRequestVOneOneTransformer);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setReferralRequest(expectedReferral);
		expected.setCaseId(4L);
		expected.setCdssSupplierId(2L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	public void shouldBuildAmendResponseResultReferralRequestV1() {
		CdssResult resultWithReferral = new CdssResult();
		IBaseReference carePlan = new Reference().setResource(
				new CarePlan()
						.addActivity(new CarePlanActivityComponent()
								.addOutcomeCodeableConcept(
										new CodeableConcept(
												new Coding("outcomeSys", "outcomeCode", "outcomeDisplay")))));
		resultWithReferral.setResult(new RequestGroup()
				.addAction(new RequestGroupActionComponent().setResource((Reference) carePlan)));
		ReferralRequest referralRequest = new ReferralRequest().setDescription("Something");
		resultWithReferral.setReferralRequest(referralRequest);
		resultWithReferral.setApiVersion(CdsApiVersion.ONE_ONE);
		ReferralRequestDTO expectedReferral = ReferralRequestDTO.builder()
				.description("Something")
				.build();

		when(implementationResolver.resolve(CdsApiVersion.ONE_ONE))
				.thenReturn(referralRequestVOneOneTransformer);
		when(referralRequestVOneOneTransformer.transform(referralRequest))
				.thenReturn(expectedReferral);

		CdssResponseDTO response = responseService
				.buildAmendResponse(resultWithReferral, null, 5L, 2L, new TriageQuestion[0]);

		verifyZeroInteractions(referralRequestVTwoTransformer, errorMessageTransformer);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setReferralRequest(expectedReferral);
		expected.setResult("outcomeDisplay");
		expected.setCaseId(5L);
		expected.setCdssSupplierId(2L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	public void shouldBuildAmendResponseResultReferralRequestV2() {
		CdssResult resultWithReferral = new CdssResult();
		IBaseReference carePlan = new Reference().setResource(
				new CarePlan()
						.addActivity(new CarePlanActivityComponent()
								.addOutcomeCodeableConcept(
										new CodeableConcept(
												new Coding("outcomeSys", "outcomeCode", "outcomeDisplay")))));
		resultWithReferral.setResult(new RequestGroup()
				.addAction(new RequestGroupActionComponent().setResource((Reference) carePlan)));
		ReferralRequest referralRequest = new ReferralRequest().setDescription("Something");
		resultWithReferral.setReferralRequest(referralRequest);
		resultWithReferral.setApiVersion(CdsApiVersion.TWO);
		ReferralRequestDTO expectedReferral = ReferralRequestDTO.builder()
				.description("Something")
				.build();

		when(implementationResolver.resolve(CdsApiVersion.TWO))
				.thenReturn(referralRequestVTwoTransformer);
		when(referralRequestVTwoTransformer.transform(referralRequest))
				.thenReturn(expectedReferral);

		CdssResponseDTO response = responseService
				.buildAmendResponse(resultWithReferral, null, 5L, 2L, new TriageQuestion[0]);

		verifyZeroInteractions(referralRequestVOneOneTransformer, errorMessageTransformer);
		CdssResponseDTO expected = new CdssResponseDTO();
		expected.setReferralRequest(expectedReferral);
		expected.setResult("outcomeDisplay");
		expected.setCaseId(5L);
		expected.setCdssSupplierId(2L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	public void shouldBuildAmendResponseQuestionnaireReferralRequestV1() {
		CdssResult resultWithQuestionnaire = new CdssResult();
		ReferralRequest referralRequest = new ReferralRequest().setDescription("Something");
		resultWithQuestionnaire.setReferralRequest(referralRequest);
		resultWithQuestionnaire.setApiVersion(CdsApiVersion.ONE_ONE);
		ReferralRequestDTO expectedReferral = ReferralRequestDTO.builder()
				.description("Something")
				.build();
		QuestionnaireItemOptionComponent option1 = new QuestionnaireItemOptionComponent()
				.setValue(new Coding("optionSys", "optionCode", "optionDisplay"));
		QuestionnaireItemOptionComponent option2 = new QuestionnaireItemOptionComponent()
				.setValue(new Coding("optionSys2", "optionCode2", "optionDisplay2"));
		Questionnaire questionnaire = new Questionnaire()
				.setStatus(PublicationStatus.ACTIVE)
				.addItem(new QuestionnaireItemComponent()
						.setLinkId("1")
						.setType(QuestionnaireItemType.CHOICE)
						.setText("Test question")
						.addOption(option1)
						.addOption(option2));
		questionnaire.setId("1");

		when(implementationResolver.resolve(CdsApiVersion.ONE_ONE))
				.thenReturn(referralRequestVOneOneTransformer);
		when(referralRequestVOneOneTransformer.transform(referralRequest))
				.thenReturn(expectedReferral);
		when(optionValueTransformer.transform(option1))
				.thenReturn(new TriageOption("sys", "optionCode", "optionDisplay"));
		when(optionValueTransformer.transform(option2))
				.thenReturn(new TriageOption("sys2", "optionCode2", "optionDisplay2"));

		CdssResponseDTO response = responseService
				.buildAmendResponse(resultWithQuestionnaire,
						questionnaire,
						5L,
						2L,
						new TriageQuestion[0]);

		verifyZeroInteractions(referralRequestVTwoTransformer, errorMessageTransformer);
		CdssResponseDTO expected = new CdssResponseDTO();
		TriageQuestion expectedQuestion = new TriageQuestion();
		expectedQuestion.setQuestion("Test question");
		expectedQuestion.setQuestionnaireId("1");
		expectedQuestion.setQuestionId("1");
		expectedQuestion.setQuestionType("CHOICE");
		expectedQuestion.addOption("sys", "optionCode", "optionDisplay");
		expectedQuestion.addOption("sys2", "optionCode2", "optionDisplay2");
		expected.setTriageQuestions(Collections.singletonList(expectedQuestion));
		expected.setReferralRequest(expectedReferral);
		expected.setCaseId(5L);
		expected.setCdssSupplierId(2L);
		assertThat(response, sameBeanAs(expected));
	}

	@Test
	public void shouldBuildAmendResponseQuestionnaireReferralRequestV2() {
		CdssResult resultWithQuestionnaire = new CdssResult();
		ReferralRequest referralRequest = new ReferralRequest().setDescription("Something");
		resultWithQuestionnaire.setReferralRequest(referralRequest);
		resultWithQuestionnaire.setApiVersion(CdsApiVersion.TWO);
		ReferralRequestDTO expectedReferral = ReferralRequestDTO.builder()
				.description("Something")
				.build();
		QuestionnaireItemOptionComponent option1 = new QuestionnaireItemOptionComponent()
				.setValue(new Coding("optionSys", "optionCode", "optionDisplay"));
		QuestionnaireItemOptionComponent option2 = new QuestionnaireItemOptionComponent()
				.setValue(new Coding("optionSys2", "optionCode2", "optionDisplay2"));
		Questionnaire questionnaire = new Questionnaire()
				.setStatus(PublicationStatus.ACTIVE)
				.addItem(new QuestionnaireItemComponent()
						.setLinkId("1")
						.setType(QuestionnaireItemType.CHOICE)
						.setText("Test question")
						.addOption(option1)
						.addOption(option2));
		questionnaire.setId("1");

		when(implementationResolver.resolve(CdsApiVersion.TWO))
				.thenReturn(referralRequestVTwoTransformer);
		when(referralRequestVTwoTransformer.transform(referralRequest))
				.thenReturn(expectedReferral);
		when(optionValueTransformer.transform(option1))
				.thenReturn(new TriageOption("sys", "optionCode", "optionDisplay"));
		when(optionValueTransformer.transform(option2))
				.thenReturn(new TriageOption("sys2", "optionCode2", "optionDisplay2"));

		CdssResponseDTO response = responseService
				.buildAmendResponse(resultWithQuestionnaire,
						questionnaire,
						5L,
						2L,
						new TriageQuestion[0]);

		verifyZeroInteractions(referralRequestVOneOneTransformer, errorMessageTransformer);
		CdssResponseDTO expected = new CdssResponseDTO();
		TriageQuestion expectedQuestion = new TriageQuestion();
		expectedQuestion.setQuestion("Test question");
		expectedQuestion.setQuestionnaireId("1");
		expectedQuestion.setQuestionId("1");
		expectedQuestion.setQuestionType("CHOICE");
		expectedQuestion.addOption("sys", "optionCode", "optionDisplay");
		expectedQuestion.addOption("sys2", "optionCode2", "optionDisplay2");
		expected.setTriageQuestions(Collections.singletonList(expectedQuestion));
		expected.setReferralRequest(expectedReferral);
		expected.setCaseId(5L);
		expected.setCdssSupplierId(2L);
		assertThat(response, sameBeanAs(expected));
	}

}
