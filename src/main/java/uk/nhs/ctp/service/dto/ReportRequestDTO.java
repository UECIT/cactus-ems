package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.ReferralRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.uhn.fhir.context.FhirContext;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public class ReportRequestDTO {
	private Long caseId;
	private String resourceUrl;
	private String handoverJson;
	
	@JsonIgnore
	private ReferralRequest referralRequest;
	@JsonIgnore
	private Bundle bundle;
	
	public String getHandoverJson() {
		return handoverJson;
	}

	public void setHandoverJson(String handoverJson) {
		this.handoverJson = handoverJson;
		this.setReferralRequest(
				FhirContext.forDstu3().newJsonParser().parseResource(ReferralRequest.class, this.handoverJson));
		this.setBundle(ResourceProviderUtils.getResources(referralRequest.getContained(), Bundle.class).get(0));
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
	}
	public ReferralRequest getReferralRequest() {
		return referralRequest;
	}
	public void setReferralRequest(ReferralRequest referralRequest) {
		this.referralRequest = referralRequest;
	}
	public Bundle getBundle() {
		return bundle;
	}
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
}
