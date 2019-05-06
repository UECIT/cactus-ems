package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.Coding;

public class ExtensionDTO {
	private String system;
	private String code;
	private String display;

	public ExtensionDTO(Coding code2) {
		this.setSystem(code2.getSystem());
		this.setCode(code2.getCode());
		this.setDisplay(code2.getDisplay());
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

}
