package uk.nhs.ctp.service.dto;

import java.util.HashSet;
import java.util.Set;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public class ReportRequestDTO {
	private Long caseId;
	private String resourceUrl;
	private String handoverJson;
	
	@JsonIgnore
	private Set<Class<?>> templateMappingExclusions = new HashSet<>();
	@JsonIgnore
	private IParser fhirParser;
	@JsonIgnore
	private ReferralRequest referralRequest;
	@JsonIgnore
	private Bundle bundle;
	
	public String getHandoverJson() {
		return handoverJson;
	}

	public void setHandoverJson(String handoverJson) {
		this.handoverJson = handoverJson;
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
		referralRequest = referralRequest == null ? 
				parseResource(ReferralRequest.class) : referralRequest;
		
		return referralRequest;
	}
	
	public Bundle getBundle() {
		bundle = bundle == null ? 
				ResourceProviderUtils.getResources(getReferralRequest().getContained(), Bundle.class).get(0) : bundle;
		
		return bundle;
	}
	
	public void setTemplateMappingExclusions(Set<Class<?>> templateMappingExclusions) {
		this.templateMappingExclusions = templateMappingExclusions;
	}
	
	public void setFhirParser(IParser fhirParser) {
		this.fhirParser = fhirParser;
	}
	
	public boolean isExcluded(Class<?> mapperClass) {
		return templateMappingExclusions.contains(mapperClass);
	}
	
	@Override
	public ReportRequestDTO clone() {
		ReportRequestDTO clone = new ReportRequestDTO();
		clone.setFhirParser(fhirParser);
		clone.setCaseId(caseId);
		clone.setHandoverJson(handoverJson);
		clone.setTemplateMappingExclusions(templateMappingExclusions);
		clone.setResourceUrl(resourceUrl);
		
		return clone;
	}

	private <RESOURCE extends Resource> RESOURCE parseResource(Class<RESOURCE> resourceClass) {
		return handoverJson == null || fhirParser == null ? 
				null : fhirParser.parseResource(resourceClass, handoverJson);
	}
	
}
