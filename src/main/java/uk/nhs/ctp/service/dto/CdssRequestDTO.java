package uk.nhs.ctp.service.dto;

public class CdssRequestDTO {

	private String questionnaireId;
	private Long caseId;
	private Long cdssSupplierId;
	private String serviceDefinitionId;
	private TriageQuestion[] questionResponse;
	private SettingsDTO settings;
	private Boolean amendingPrevious;

	public Long getCaseId() {
		return caseId;
	}

	public Long getCdssSupplierId() {
		return cdssSupplierId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
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

	public TriageQuestion[] getQuestionResponse() {
		return questionResponse;
	}

	public void setQuestionResponse(TriageQuestion[] questionResponse) {
		this.questionResponse = questionResponse;
	}

	public SettingsDTO getSettings() {
		return settings;
	}

	public void setSettings(SettingsDTO settings) {
		this.settings = settings;
	}

	public Boolean isAmendingPrevious() {
		return amendingPrevious;
	}

	public void setAmendingPrevious(Boolean amendingPrevious) {
		this.amendingPrevious = amendingPrevious;
	}

	public String getQuestionnaireId() {
		return questionnaireId;
	}

	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}
}
