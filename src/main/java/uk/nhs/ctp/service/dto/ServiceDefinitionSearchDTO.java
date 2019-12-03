package uk.nhs.ctp.service.dto;

public class ServiceDefinitionSearchDTO {

  private Long caseId;
  private Long patientId;
  private SettingsDTO settings;

  public Long getCaseId() {
    return caseId;
  }

  public void setCaseId(Long caseId) {
    this.caseId = caseId;
  }

  public Long getPatientId() {
    return patientId;
  }

  public void setPatientId(Long patientId) {
    this.patientId = patientId;
  }

  public SettingsDTO getSettings() {
    return settings;
  }

  public void setSettings(SettingsDTO settings) {
    this.settings = settings;
  }

}
