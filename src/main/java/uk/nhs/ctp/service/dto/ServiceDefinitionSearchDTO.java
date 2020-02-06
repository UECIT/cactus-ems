package uk.nhs.ctp.service.dto;

import lombok.Data;

@Data
public class ServiceDefinitionSearchDTO {

  private Long caseId;
  private String patientId;
  private SettingsDTO settings;

}
