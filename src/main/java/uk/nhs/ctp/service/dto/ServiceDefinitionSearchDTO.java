package uk.nhs.ctp.service.dto;

import lombok.Data;

@Data
public class ServiceDefinitionSearchDTO {

  private String patientId;
  private SettingsDTO settings;

}
