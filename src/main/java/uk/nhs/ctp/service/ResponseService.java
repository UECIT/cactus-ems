package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import uk.nhs.ctp.OperationOutcomeFactory;
import uk.nhs.ctp.SystemCode;
import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.ExtensionDTO;
import uk.nhs.ctp.service.dto.ProcedureRequestDTO;
import uk.nhs.ctp.service.dto.ReferralRequestDTO;
import uk.nhs.ctp.service.dto.TriageOption;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Service
public class ResponseService {

	private static final Logger LOG = LoggerFactory.getLogger(ResponseService.class);

	/**
	 * Build response DTO with a summary of the CDSS response
	 * 
	 * @param cdssResult     {@link CdssResult}
	 * @param questionnaire  {@link Questionnaire}
	 * @param caseId         Case ID
	 * @param cdssSupplierId CDSS supplier ID
	 * @return {@link CdssResponseDTO}
	 */
	public CdssResponseDTO buildResponse(CdssResult cdssResult, Questionnaire questionnaire, Long caseId,
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
			response.setReferralRequest(new ReferralRequestDTO(cdssResult.getReferralRequest()));
		}
		if (cdssResult.hasCareAdvice()) {
			response.setCareAdvice(cdssResult.getCareAdvice());
		}
		if (cdssResult.hasProcedureRequest()) {
			response.setProcedureRequest(new ProcedureRequestDTO(cdssResult.getProcedureRequest()));
		}

		return response;
	}

	/**
	 * Build response DTO with a summary of the CDSS response
	 * 
	 * @param cdssResult       {@link CdssResult}
	 * @param questionnaire    {@link Questionnaire}
	 * @param caseId           Case ID
	 * @param cdssSupplierId   CDSS supplier ID
	 * @param previousResponse
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
				response.setReferralRequest(new ReferralRequestDTO(cdssResult.getReferralRequest()));
			}
		}

		return response;
	}

	/**
	 * Add result to the CDSS response
	 * 
	 * @param cdssResult
	 * @param response
	 * @throws FHIRException
	 */
	void addCdssResult(CdssResult cdssResult, CdssResponseDTO response) throws FHIRException {
		if (cdssResult.hasResult()) {
			CarePlan careplan = ResourceProviderUtils
					.castToType(cdssResult.getResult().getActionFirstRep().getResource().getResource(), CareConnectCarePlan.class);
			setResult(careplan, response);
			if (cdssResult.hasTrigger()) {
				response.setSwitchTrigger(cdssResult.getSwitchTrigger());
			}
			if (cdssResult.hasReferralRequest()) {
				response.setReferralRequest(new ReferralRequestDTO(cdssResult.getReferralRequest()));
			}
			if (cdssResult.getCareAdvice() != null) {
				response.setCareAdvice(cdssResult.getCareAdvice());
			}
		}
	}

	/**
	 * Add basic details to CdssResponse
	 * 
	 * @param caseId
	 * @param cdssSupplierId
	 * @param serviceDefinitionId
	 * @param response
	 */
	void setTriageRequestDetails(Long caseId, Long cdssSupplierId, String serviceDefinitionId,
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
					if (question.getExtensionFirstRep().getValue() instanceof Coding) {
						Coding code = (Coding) question.getExtensionFirstRep().getValue();
						ExtensionDTO ex = new ExtensionDTO(code);
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

				question.getOption().forEach(option -> {
					try {
						Coding optionCode = option.getValueCoding();
						if (optionCode != null) {
							if (option.getExtensionFirstRep().isEmpty()) {
								triageQuestion.addOption(optionCode.getCode(), optionCode.getDisplay());
							} else {
								triageQuestion.addOption(optionCode.getCode(), optionCode.getDisplay(),
										option.getExtensionFirstRep());
							}
						}
					} catch (FHIRException e) {
						LOG.error("Could not get value coding", e);
					}
				});
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
				if (question.getExtensionFirstRep().getValue() instanceof Coding) {
					Coding code = (Coding) question.getExtensionFirstRep().getValue();
					ExtensionDTO ex = new ExtensionDTO(code);
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

			question.getOption().forEach(option -> {
				try {
					Coding optionCode = option.getValueCoding();
					if (optionCode != null) {
						if (option.getExtensionFirstRep().isEmpty()) {
							triageQuestion.addOption(optionCode.getCode(), optionCode.getDisplay());
						} else {
							triageQuestion.addOption(optionCode.getCode(), optionCode.getDisplay(),
									option.getExtensionFirstRep());
						}
					}
				} catch (FHIRException e) {
					LOG.error("Could not get value coding", e);
				}
			});
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

}
