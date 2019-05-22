package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import resources.CareConnectOrganization;
import resources.CareConnectPatient;
import resources.CareConnectPractitioner;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public class ReportRequestDTO {
	private Long caseId;
	private String resourceUrl;
	private String handoverJson;
	private Set<Class<?>> templateMappingExclusions = new HashSet<>();

	@JsonIgnore
	private ReferralRequest referralRequest;
	@JsonIgnore
	private Bundle bundle;
	
	public String getHandoverJson() {
		return handoverJson;
	}

	public void setHandoverJson(String handoverJson) {
		this.handoverJson = handoverJson;
		
		List<Class<? extends IBaseResource>> resourceClasses = new ArrayList<>();
		resourceClasses.add(CareConnectPatient.class);
		resourceClasses.add(CareConnectOrganization.class);
		resourceClasses.add(CareConnectPractitioner.class);
		
		IParser fhirParser = FhirContext.forDstu3().newJsonParser();
		fhirParser.setPreferTypes(resourceClasses);
		
		setReferralRequest(fhirParser.parseResource(ReferralRequest.class, this.handoverJson));
		setBundle(ResourceProviderUtils.getResources(referralRequest.getContained(), Bundle.class).get(0));
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
	
	public void setTemplateMappingExclusions(Set<Class<?>> templateMappingExclusions) {
		this.templateMappingExclusions = templateMappingExclusions;
	}
	
	public boolean isExcluded(Class<?> mapperClass) {
		return templateMappingExclusions.contains(mapperClass);
	}
	
	@Override
	public ReportRequestDTO clone() {
		ReportRequestDTO clone = new ReportRequestDTO();
		clone.setCaseId(caseId);
		clone.setHandoverJson(handoverJson);
		clone.setTemplateMappingExclusions(templateMappingExclusions);
		clone.setResourceUrl(resourceUrl);
		
		return clone;
	}
	
}
