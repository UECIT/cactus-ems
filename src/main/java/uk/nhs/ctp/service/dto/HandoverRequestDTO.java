package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.Bundle;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class HandoverRequestDTO {
	private Long caseId;
	private String remoteUrl;
	private String resourceUrl;
	@JsonIgnore
	private Bundle resourceBundle;
	
	public String getRemoteUrl() {
		return remoteUrl;
	}
	public boolean hasRemoteUrl() {
		return remoteUrl != null;
	}
	public Long getCaseId() {
		return caseId;
	}
	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}
	public String getResourceUrl() {
		return resourceUrl;
	}
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
		
		if (resourceUrl.startsWith("http")) {
			String[] componentParts = resourceUrl.split("/");
			remoteUrl = resourceUrl.substring(
					0, resourceUrl.indexOf(componentParts[componentParts.length - 2]) - 1);
		}
	}
	public Bundle getResourceBundle() {
		return resourceBundle;
	}
	public void setResourceBundle(Bundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}
}
