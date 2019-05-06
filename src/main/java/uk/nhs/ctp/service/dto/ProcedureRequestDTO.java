package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.ProcedureRequest;

public class ProcedureRequestDTO {
	private String text;
	private String codeSystem;
	private String codeCode;
	private String codeDisplay;
	private String reasonCodeSystem;
	private String reasonCodeCode;
	private String reasonCodeDisplay;
	
	public ProcedureRequestDTO(String text, String codeSystem, String codeCode, String codeDisplay,
			String reasonCodeSystem, String reasonCodeCode, String reasonCodeDisplay) {
		super();
		this.text = text;
		this.codeSystem = codeSystem;
		this.codeCode = codeCode;
		this.codeDisplay = codeDisplay;
		this.reasonCodeSystem = reasonCodeSystem;
		this.reasonCodeCode = reasonCodeCode;
		this.reasonCodeDisplay = reasonCodeDisplay;
	}
	
	public ProcedureRequestDTO(ProcedureRequest procedureRequest) {
		super();
		this.text = procedureRequest.getText().getDiv().getChildNodes().get(0).getContent();
		this.codeSystem = procedureRequest.getCode().getCodingFirstRep().getSystem();
		this.codeCode = procedureRequest.getCode().getCodingFirstRep().getCode();
		this.codeDisplay = procedureRequest.getCode().getCodingFirstRep().getDisplay();
		this.reasonCodeSystem = procedureRequest.getReasonCodeFirstRep().getCodingFirstRep().getSystem();
		this.reasonCodeCode = procedureRequest.getReasonCodeFirstRep().getCodingFirstRep().getCode();
		this.reasonCodeDisplay = procedureRequest.getReasonCodeFirstRep().getCodingFirstRep().getDisplay();
	}
	
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCodeSystem() {
		return codeSystem;
	}
	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}
	public String getCodeCode() {
		return codeCode;
	}
	public void setCodeCode(String codeCode) {
		this.codeCode = codeCode;
	}
	public String getCodeDisplay() {
		return codeDisplay;
	}
	public void setCodeDisplay(String codeDisplay) {
		this.codeDisplay = codeDisplay;
	}
	public String getReasonCodeSystem() {
		return reasonCodeSystem;
	}
	public void setReasonCodeSystem(String reasonCodeSystem) {
		this.reasonCodeSystem = reasonCodeSystem;
	}
	public String getReasonCodeCode() {
		return reasonCodeCode;
	}
	public void setReasonCodeCode(String reasonCodeCode) {
		this.reasonCodeCode = reasonCodeCode;
	}
	public String getReasonCodeDisplay() {
		return reasonCodeDisplay;
	}
	public void setReasonCodeDisplay(String reasonCodeDisplay) {
		this.reasonCodeDisplay = reasonCodeDisplay;
	}

}
