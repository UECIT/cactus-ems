package uk.nhs.ctp.service.dto;

public class SettingsDTO {
	private PersonDTO initiatingPerson;
	private CodeDTO userType;
	private CodeDTO userLanguage;
	private CodeDTO userTaskContext;
	private PersonDTO receivingPerson;
	private CodeDTO recipientType;
	private CodeDTO recipientLanguage;
	private CodeDTO setting;

	public PersonDTO getInitiatingPerson() {
		return initiatingPerson;
	}

	public void setInitiatingPerson(PersonDTO initiatingPerson) {
		this.initiatingPerson = initiatingPerson;
	}

	public CodeDTO getUserType() {
		return userType;
	}

	public void setUserType(CodeDTO userType) {
		this.userType = userType;
	}

	public CodeDTO getUserLanguage() {
		return userLanguage;
	}

	public void setUserLanguage(CodeDTO userLanguage) {
		this.userLanguage = userLanguage;
	}

	public CodeDTO getUserTaskContext() {
		return userTaskContext;
	}

	public void setUserTaskContext(CodeDTO userTaskContext) {
		this.userTaskContext = userTaskContext;
	}

	public PersonDTO getReceivingPerson() {
		return receivingPerson;
	}

	public void setReceivingPerson(PersonDTO receivingPerson) {
		this.receivingPerson = receivingPerson;
	}

	public CodeDTO getRecipientType() {
		return recipientType;
	}

	public void setRecipientType(CodeDTO recipientType) {
		this.recipientType = recipientType;
	}

	public CodeDTO getRecipientLanguage() {
		return recipientLanguage;
	}

	public void setRecipientLanguage(CodeDTO recipientLanguage) {
		this.recipientLanguage = recipientLanguage;
	}

	public CodeDTO getSetting() {
		return setting;
	}

	public void setSetting(CodeDTO setting) {
		this.setting = setting;
	}
}
