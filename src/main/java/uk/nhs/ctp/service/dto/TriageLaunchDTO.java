package uk.nhs.ctp.service.dto;

import lombok.Data;

@Data
public class TriageLaunchDTO {

	private Long patientId;
	private Long cdssSupplierId;
	private String serviceDefinitionId;
	private SettingsDTO settings;
	private String encounterId;

}
