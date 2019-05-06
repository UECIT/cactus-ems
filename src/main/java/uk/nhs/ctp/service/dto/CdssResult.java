package uk.nhs.ctp.service.dto;

import java.util.List;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;

public class CdssResult {

	private List<Resource> outputData;
	private RequestGroup result;
	private String questionnaireRef;
	private String serviceDefinitionId;
	private String switchTrigger;
	private ReferralRequest referralRequest;
	private List<CarePlanDTO> careAdvice;
	private String sessionId;
	private ProcedureRequest procedureRequest;
	private List<Resource> contained;

	public List<Resource> getOutputData() {
		return outputData;
	}

	public RequestGroup getResult() {
		return result;
	}

	public String getQuestionnaireRef() {
		return questionnaireRef;
	}

	public void setOutputData(List<Resource> outputData) {
		this.outputData = outputData;
	}

	public void setResult(RequestGroup result) {
		this.result = result;
	}

	public void setQuestionnaireId(String questionnaireRef) {
		this.questionnaireRef = questionnaireRef;
	}

	public boolean hasOutputData() {
		return this.outputData != null && !this.outputData.isEmpty();
	}

	public boolean hasResult() {
		return this.result != null;
	}

	public boolean hasTrigger() {
		return this.switchTrigger != null;
	}

	public boolean hasQuestionnaire() {
		return this.questionnaireRef != null;
	}

	public boolean isInProgress() {
		return this.hasOutputData() && !this.hasResult() && !this.hasQuestionnaire();
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getSwitchTrigger() {
		return switchTrigger;
	}

	public void setSwitchTrigger(String switchTrigger) {
		this.switchTrigger = switchTrigger;
	}

	public ReferralRequest getReferralRequest() {
		return referralRequest;
	}

	public void setReferralRequest(ReferralRequest referralRequest) {
		this.referralRequest = referralRequest;
	}

	public boolean hasReferralRequest() {
		return this.referralRequest != null;
	}

	public List<CarePlanDTO> getCareAdvice() {
		return careAdvice;
	}

	public void setCareAdvice(List<CarePlanDTO> careAdvice) {
		this.careAdvice = careAdvice;
	}
	
	public boolean hasCareAdvice() {
		return this.careAdvice != null;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public ProcedureRequest getProcedureRequest() {
		return procedureRequest;
	}

	public void setProcedureRequest(ProcedureRequest procedureRequest) {
		this.procedureRequest = procedureRequest;
	}
	
	public boolean hasProcedureRequest() {
		return procedureRequest != null;
	}

	public List<Resource> getContained() {
		return contained;
	}

	public void setContained(List<Resource> contained) {
		this.contained = contained;
	}

}
