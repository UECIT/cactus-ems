package uk.nhs.ctp.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class CdssResponseDTO {

	private Long caseId;
	private Long cdssSupplierId;
	private String serviceDefinitionId;
	@JsonInclude(Include.NON_NULL)
	private List<TriageQuestion> triageQuestions;
	@JsonInclude(Include.NON_NULL)
	private String result;
	private String switchTrigger;
	private ReferralRequestDTO referralRequest;
	private List<CarePlanDTO> careAdvice;
	private ProcedureRequestDTO procedureRequest;

	public Long getCaseId() {
		return caseId;
	}

	public Long getCdssSupplierId() {
		return cdssSupplierId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public String getResult() {
		return result;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public void setCdssSupplierId(Long cdssSupplierId) {
		this.cdssSupplierId = cdssSupplierId;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSwitchTrigger() {
		return switchTrigger;
	}

	public void setSwitchTrigger(String switchTrigger) {
		this.switchTrigger = switchTrigger;
	}

	public ReferralRequestDTO getReferralRequest() {
		return referralRequest;
	}

	public void setReferralRequest(ReferralRequestDTO referralRequest) {
		this.referralRequest = referralRequest;
	}

	public List<TriageQuestion> getTriageQuestions() {
		return triageQuestions;
	}

	public void setTriageQuestions(List<TriageQuestion> triageQuestions) {
		this.triageQuestions = triageQuestions;
	}

	public List<CarePlanDTO> getCareAdvice() {
		return careAdvice;
	}

	public void setCareAdvice(List<CarePlanDTO> careAdvice) {
		this.careAdvice = careAdvice;
	}

	public ProcedureRequestDTO getProcedureRequest() {
		return procedureRequest;
	}

	public void setProcedureRequest(ProcedureRequestDTO procedureRequest) {
		this.procedureRequest = procedureRequest;
	}

}
