package uk.nhs.ctp.service.dto;

public class TriageExtension {

	private String url;
	private String value;

	public TriageExtension() {

	}

	public TriageExtension(String url, String value) {
		this.setUrl(url);
		this.setValue(value);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
