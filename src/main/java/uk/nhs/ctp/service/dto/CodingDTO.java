package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.CodeableConcept;

public class CodingDTO {
	private String serviceRequestedSystem;
	private String serviceRequestedCode;
	private String serviceRequestedDisplay;

	public CodingDTO(CodeableConcept coding) {
		this.setServiceRequestedSystem(coding.getCodingFirstRep().getSystem());
		this.setServiceRequestedCode(coding.getCodingFirstRep().getCode());
		this.setServiceRequestedDisplay(coding.getCodingFirstRep().getDisplay());
	}

	public String getServiceRequestedSystem() {
		return serviceRequestedSystem;
	}

	public void setServiceRequestedSystem(String serviceRequestedSystem) {
		this.serviceRequestedSystem = serviceRequestedSystem;
	}

	public String getServiceRequestedCode() {
		return serviceRequestedCode;
	}

	public void setServiceRequestedCode(String serviceRequestedCode) {
		this.serviceRequestedCode = serviceRequestedCode;
	}

	public String getServiceRequestedDisplay() {
		return serviceRequestedDisplay;
	}

	public void setServiceRequestedDisplay(String serviceRequestedDisplay) {
		this.serviceRequestedDisplay = serviceRequestedDisplay;
	}
}
