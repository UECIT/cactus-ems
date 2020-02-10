package uk.nhs.ctp.service.dto;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public class ReportRequestDTO {

  private Long caseId;
  private String resourceUrl;
  private String handoverJson;

  @JsonIgnore
  private Set<Class<?>> templateMappingExclusions = new HashSet<>();
  @JsonIgnore
  private FhirContext fhirContext;
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
        ResourceProviderUtils.getResources(getReferralRequest().getContained(), Bundle.class).get(0)
        : bundle;

    return bundle;
  }

  public void setTemplateMappingExclusions(Set<Class<?>> templateMappingExclusions) {
    this.templateMappingExclusions = templateMappingExclusions;
  }

  public void setFhirContext(FhirContext fhirContext) {
    this.fhirContext = fhirContext;
  }

  public boolean isExcluded(Class<?> mapperClass) {
    return templateMappingExclusions.contains(mapperClass);
  }

  @Override
  public ReportRequestDTO clone() {
    ReportRequestDTO clone = new ReportRequestDTO();
    clone.setFhirContext(fhirContext);
    clone.setCaseId(caseId);
    clone.setHandoverJson(handoverJson);
    clone.setTemplateMappingExclusions(templateMappingExclusions);
    clone.setResourceUrl(resourceUrl);

    return clone;
  }

  private <RESOURCE extends Resource> RESOURCE parseResource(Class<RESOURCE> resourceClass) {
    if (handoverJson == null || fhirContext == null) {
      return null;
    }

    IParser jsonParser = fhirContext.newJsonParser();
    if (resourceUrl != null) {
      String baseUrl = new IdType(resourceUrl).getBaseUrl();
      if (StringUtils.isNotEmpty(baseUrl)) {
        jsonParser.setServerBaseUrl(baseUrl);
      }
    }
    return jsonParser.parseResource(resourceClass, handoverJson);
  }

}
