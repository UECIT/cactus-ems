package uk.nhs.ctp.service.dto;

import lombok.Data;

@Data
public class SettingsDTO {

	private CodeDTO userType;
	private CodeDTO userLanguage;
	private CodeDTO userTaskContext;
	private CodeDTO recipientLanguage;
	private CodeDTO setting;
	private CodeDTO jurisdiction;
	private PractitionerDTO practitioner;

}
