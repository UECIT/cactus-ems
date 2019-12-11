package uk.nhs.ctp.service.dto;

import lombok.Data;

@Data
public class SettingsDTO {

	private PersonDTO initiatingPerson;
	private CodeDTO userType;
	private CodeDTO userLanguage;
	private CodeDTO userTaskContext;
	private PersonDTO receivingPerson;
	private CodeDTO recipientType;
	private CodeDTO recipientLanguage;
	private CodeDTO setting;
	private CodeDTO jurisdiction;

}
