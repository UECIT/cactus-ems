package uk.nhs.ctp.service;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.OperationOutcomeFactory;
import uk.nhs.ctp.SystemCode;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.ExtensionDTO;
import uk.nhs.ctp.service.dto.TriageOption;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.transform.ErrorMessageTransformer;
import uk.nhs.ctp.transform.QuestionnaireOptionValueTransformer;
import uk.nhs.ctp.transform.ReferralRequestDTOTransformer;
import uk.nhs.ctp.utils.ImplementationResolver;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Service
@RequiredArgsConstructor
public class ResponseService {

	private final ErrorMessageTransformer errorMessageTransformer;
	private final ImplementationResolver<ReferralRequestDTOTransformer> referralRequestTransformerResolver;
	private final QuestionnaireOptionValueTransformer optionValueTransformer;

	/**
	 * Build response DTO with a summary of the CDSS response
	 * 
	 * @param cdssResult     {@link CdssResult}
	 * @param questionnaire  {@link Questionnaire}
	 * @param caseId         Case ID
	 * @param cdssSupplierId CDSS supplier ID
	 * @return {@link CdssResponseDTO}
	 */
	public CdssResponseDTO buildResponse(
			CdssResult cdssResult,
			Questionnaire questionnaire,
			Long caseId,
			Long cdssSupplierId) throws FHIRException {
		CdssResponseDTO response = new CdssResponseDTO();

		setTriageRequestDetails(caseId, cdssSupplierId, cdssResult.getServiceDefinitionId(), response);
		if (cdssResult.hasResult()) {
			setResult(cdssResult.getResult().getActionFirstRep().getResource().getResource(), response);
		} else if (questionnaire != null) {
			setTriageQuestion(questionnaire, response, null);
		}

		if (cdssResult.hasTrigger()) {
			response.setSwitchTrigger(cdssResult.getSwitchTrigger());
		}
		
		if (cdssResult.hasReferralRequest()) {
			var referralRequestDTOTransformer = resolveTransformer(cdssResult.getApiVersion());
			response.setReferralRequest(referralRequestDTOTransformer.transform(cdssResult.getReferralRequest()));
		}
		if (cdssResult.hasCareAdvice()) {
			response.setCareAdvice(cdssResult.getCareAdvice());
		}

		response.setErrorMessage(errorMessageTransformer.transform(cdssResult.getOperationOutcome()));

		return response;
	}

	/**
	 * Build response DTO with a summary of the CDSS response
	 * 
	 * @param cdssResult       {@link CdssResult}
	 * @param questionnaire    {@link Questionnaire}
	 * @param caseId           Case ID
	 * @param cdssSupplierId   CDSS supplier ID
	 * @param previousQuestions {@link TriageQuestion[]}
	 * @return {@link CdssResponseDTO}
	 */
	public CdssResponseDTO buildAmendResponse(CdssResult cdssResult, Questionnaire questionnaire, Long caseId,
			Long cdssSupplierId, TriageQuestion[] previousQuestions) throws FHIRException {
		CdssResponseDTO response = new CdssResponseDTO();
		setTriageRequestDetails(caseId, cdssSupplierId, cdssResult.getServiceDefinitionId(), response);
		addCdssResult(cdssResult, response);
		if (questionnaire != null) {

			Map<String, Object> triageResponses = new HashMap<>();
			for (TriageQuestion previousQuestion : previousQuestions) {
				if (previousQuestion.getResponse() == null) {
					triageResponses.put(previousQuestion.getQuestionId(), previousQuestion.getResponseString());
				} else {
					triageResponses.put(previousQuestion.getQuestionId(), previousQuestion.getResponse());
				}
			}

			setTriageQuestion(questionnaire, response, triageResponses);
			if (cdssResult.hasReferralRequest()) {
				var referralRequestDTOTransformer = resolveTransformer(cdssResult.getApiVersion());
				response.setReferralRequest(referralRequestDTOTransformer.transform(cdssResult.getReferralRequest()));
			}
		}

		return response;
	}

	private void addCdssResult(CdssResult cdssResult, CdssResponseDTO response) throws FHIRException {
		if (cdssResult.hasResult()) {
			CarePlan careplan = ResourceProviderUtils
					.castToType(cdssResult.getResult().getActionFirstRep().getResource().getResource(), CarePlan.class);
			setResult(careplan, response);
			if (cdssResult.hasTrigger()) {
				response.setSwitchTrigger(cdssResult.getSwitchTrigger());
			}
			if (cdssResult.hasReferralRequest()) {
				var referralRequestDTOTransformer = resolveTransformer(cdssResult.getApiVersion());
				response.setReferralRequest(referralRequestDTOTransformer.transform(cdssResult.getReferralRequest()));
			}
			if (cdssResult.getCareAdvice() != null) {
				response.setCareAdvice(cdssResult.getCareAdvice());
			}
		}
	}

	private void setTriageRequestDetails(Long caseId, Long cdssSupplierId, String serviceDefinitionId,
			CdssResponseDTO response) {
		response.setCaseId(caseId);
		response.setCdssSupplierId(cdssSupplierId);
		response.setServiceDefinitionId(serviceDefinitionId);
	}

	private void setTriageQuestion(Questionnaire questionnaire, CdssResponseDTO response,
			Map<String, Object> triageResponses) {

		List<TriageQuestion> triageQuestions = new ArrayList<>();

		if (questionnaire.hasItem()) {

			for (QuestionnaireItemComponent question : questionnaire.getItem()) {
				TriageQuestion triageQuestion = new TriageQuestion();
				triageQuestion.setQuestionnaireId(questionnaire.getIdElement().getIdPart());
				triageQuestion.setQuestion(question.getText());
				triageQuestion.setQuestionId(question.getLinkId());
				triageQuestion.setRepeats(question.getRepeats());
				triageQuestion.setRequired(question.getRequired());
				triageQuestion.setQuestionType(question.getType().toString());

				if (!question.getExtension().isEmpty()) {
					if (question.getExtensionFirstRep().getValue() instanceof CodeableConcept) {
						CodeableConcept code = (CodeableConcept) question.getExtensionFirstRep().getValue();
						ExtensionDTO ex = new ExtensionDTO(code.getCodingFirstRep());
						triageQuestion.setExtension(ex);
					}
				}
				
				if (question.hasEnableWhen()) {
					triageQuestion.setEnableWhenQuestionnaireId(question.getEnableWhen().get(0).getQuestion());
					triageQuestion.setEnableWhenAnswer(question.getEnableWhen().get(0).hasAnswer());
				}

				if (question.getInitial() != null) {
					Attachment intial = (Attachment) question.getInitial();
					triageQuestion.setResponseAttachmentInitial(intial.getUrl());
				}

				// check if the question contains Sub-Questions
				if (!question.getItem().isEmpty()) {
					triageQuestion.setSubQuestions(
							convertQuestion(question.getItem(), questionnaire.getIdElement().getIdPart()));
				}

				if (triageResponses != null) {
					Object questionResponse = triageResponses.get(triageQuestion.getQuestionId());
					if (questionResponse != null) {
						if (questionResponse instanceof String) {
							triageQuestion.setResponseString((String) questionResponse);
						} else if (questionResponse instanceof TriageOption) {
							triageQuestion.setResponse((TriageOption) questionResponse);
						}
					}
				}

				question.getOption().stream()
						.map(optionValueTransformer::transform)
						.forEach(triageQuestion::addOption);
				triageQuestions.add(triageQuestion);
			}
		}
		response.setTriageQuestions(triageQuestions);
	}

	private List<TriageQuestion> convertQuestion(List<QuestionnaireItemComponent> items, String QuestionnaireId) {

		List<TriageQuestion> subQuestions = new ArrayList<>();

		for (QuestionnaireItemComponent question : items) {
			TriageQuestion triageQuestion = new TriageQuestion();
			triageQuestion.setQuestionnaireId(QuestionnaireId);
			triageQuestion.setQuestion(question.getText());
			triageQuestion.setQuestionId(question.getLinkId());
			triageQuestion.setRepeats(question.getRepeats());
			triageQuestion.setRequired(question.getRequired());
			triageQuestion.setQuestionType(question.getType().toString());

			if (!question.getExtension().isEmpty()) {
				if (question.getExtensionFirstRep().getValue() instanceof CodeableConcept) {
					CodeableConcept code = (CodeableConcept) question.getExtensionFirstRep().getValue();
					ExtensionDTO ex = new ExtensionDTO(code.getCodingFirstRep());
					triageQuestion.setExtension(ex);
				}
			}
			
			if (question.hasEnableWhen()) {
				triageQuestion.setEnableWhenQuestionnaireId(question.getEnableWhen().get(0).getQuestion());
				triageQuestion.setEnableWhenAnswer(question.getEnableWhen().get(0).hasAnswer());
			}

			if (question.getInitial() != null) {
				Attachment intial = (Attachment) question.getInitial();
				triageQuestion.setResponseAttachmentInitial(intial.getUrl());
			}

			question.getOption().stream()
					.map(optionValueTransformer::transform)
					.forEach(triageQuestion::addOption);
			subQuestions.add(triageQuestion);
		}
		return subQuestions;
	}

	private void setResult(IBaseResource baseResource, CdssResponseDTO response) {

		if (baseResource instanceof CarePlan) {
			CarePlan careplan = ResourceProviderUtils.castToType(baseResource, CarePlan.class);
			if (careplan.hasActivity() && careplan.getActivityFirstRep().hasOutcomeCodeableConcept()) {
				response.setResult(careplan.getActivityFirstRep().getOutcomeCodeableConceptFirstRep()
						.getCodingFirstRep().getDisplay());
			} else {
				response.setResult(careplan.getActivityFirstRep().getDetail().getCode().getText());
			}
		} else if (baseResource instanceof ReferralRequest) {
			ReferralRequest referralRequest = ResourceProviderUtils.castToType(baseResource, ReferralRequest.class);
			response.setResult(referralRequest.getDescription());
		} else if (baseResource instanceof ActivityDefinition) {
			ActivityDefinition activityDefinition = ResourceProviderUtils.castToType(baseResource,
					ActivityDefinition.class);
			response.setResult(activityDefinition.getDescription());
		} else {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new InvalidRequestException(
							"Invalid result.activity passed in: " + baseResource.getClass().getSimpleName()),
					SystemCode.BAD_REQUEST, IssueType.INVALID);
		}

	}

	private ReferralRequestDTOTransformer resolveTransformer(CdsApiVersion version) {
		return referralRequestTransformerResolver.resolve(version);
	}

}
