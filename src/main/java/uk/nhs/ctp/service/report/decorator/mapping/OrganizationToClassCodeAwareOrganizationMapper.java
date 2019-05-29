package uk.nhs.ctp.service.report.decorator.mapping;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.dstu3.model.Coding;

import resources.CareConnectOrganization;
import uk.nhs.ctp.service.report.org.hl7.v3.CDAOrganizationTypeDisplayName;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.ClassCodeAware;
import uk.nhs.ctp.service.report.org.hl7.v3.Entity;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;

public abstract class OrganizationToClassCodeAwareOrganizationMapper<CODE extends CV, ORGANIZATION extends Entity<ON> & ClassCodeAware<CODE>> 
		extends OrganizationToOrganizationMapper<ORGANIZATION> {

	private Map<String, CDAOrganizationTypeDisplayName> 
		organizationTypeMap = new HashMap<String, CDAOrganizationTypeDisplayName>();

	public OrganizationToClassCodeAwareOrganizationMapper() {
		organizationTypeMap.put(
				"http://terminology.hl7.org/CodeSystem/organization-typeprov", 
				CDAOrganizationTypeDisplayName.GENERAL_MEDICAL_PRACTICE);
		
		organizationTypeMap.put(
				"http://terminology.hl7.org/CodeSystem/organization-typeother", 
				CDAOrganizationTypeDisplayName.NHS_DIRECT);
	}
	
	public ORGANIZATION map(CareConnectOrganization organization) {
		ORGANIZATION targetOrganization = super.map(organization);
		CDAOrganizationTypeDisplayName code = CDAOrganizationTypeDisplayName.NOT_SPECIFIED;
		
		if (!organization.getType().isEmpty()) {
			Coding organizationType = organization.getType().get(0).getCodingFirstRep();
			String organizationTypeKey = organizationType.getSystem() + organizationType.getCode();
			if (organizationTypeMap.containsKey(organizationTypeKey)) 
					code = organizationTypeMap.get(organizationTypeKey);
		}
		
		CODE classCode = createStandardIndustryClassCode();
		classCode.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.191");
		classCode.setCode("999"); //TODO remove hardcoding
		classCode.setDisplayName(code.value());
		
		targetOrganization.setStandardIndustryClassCode(classCode);
		
		return targetOrganization;
	}

	protected abstract CODE createStandardIndustryClassCode();
	
}
