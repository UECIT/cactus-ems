package uk.nhs.ctp.service.dto;

import lombok.Data;

@Data
public class ServiceDefinitionSearchDTO {

  private Long caseId;
  private Long patientId;
  private SettingsDTO settings;

}
