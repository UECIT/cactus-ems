package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.CarePlan.CarePlanActivityComponent;

public class ActivityDTO {

	private String description;
	private String display;
	private String system;
	private String code;
	private String text;
	
	public ActivityDTO() {
		
	}
	
	public ActivityDTO(CarePlanActivityComponent carePlanActivityComponent) {
		this.setDescription(carePlanActivityComponent.getDetail().getDescription());
		this.setDisplay(
				carePlanActivityComponent.getDetail().getCategory().getCodingFirstRep().getDisplay());
		this.setSystem(
				carePlanActivityComponent.getDetail().getCode().getCodingFirstRep().getSystem());
		this.setCode(carePlanActivityComponent.getDetail().getCode().getCodingFirstRep().getCode());
		this.setText(carePlanActivityComponent.getDetail().getCode().getText());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
