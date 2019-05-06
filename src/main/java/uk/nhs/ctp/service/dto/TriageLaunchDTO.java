package uk.nhs.ctp.service.dto;

public class TriageLaunchDTO {

	private Long patientId;
	private Long cdssSupplierId;
	private String serviceDefinitionId;
	private SettingsDTO settings;

	public Long getPatientId() {
		return patientId;
	}

	public Long getCdssSupplierId() {
		return cdssSupplierId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public void setCdssSupplierId(Long cdssSupplierId) {
		this.cdssSupplierId = cdssSupplierId;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public SettingsDTO getSettings() {
		return settings;
	}

	public void setSettings(SettingsDTO settings) {
		this.settings = settings;
	}

}
