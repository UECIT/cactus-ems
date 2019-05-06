package uk.nhs.ctp.service.dto;

public class TriageOption {

	private String code;

	private String display;

	private TriageExtension extension;

	public TriageOption() {

	}

	public TriageOption(String code, String display) {
		this.code = code;
		this.display = display;
		this.extension = null;
	}

	public TriageOption(String code, String display, TriageExtension extension) {
		this.code = code;
		this.display = display;
		this.extension = extension;
	}

	public String getCode() {
		return code;
	}

	public String getDisplay() {
		return display;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public TriageExtension getExtension() {
		return extension;
	}

	public void setExtension(TriageExtension extension) {
		this.extension = extension;
	}

}
