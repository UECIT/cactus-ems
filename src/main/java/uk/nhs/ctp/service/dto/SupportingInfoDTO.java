package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.Reference;

public class SupportingInfoDTO {
	private String reference;
	private String display;

	public SupportingInfoDTO(Reference info) {
		this.setReference(info.getReference());
		this.setDisplay(info.getDisplay());
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

}
