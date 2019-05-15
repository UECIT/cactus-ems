package uk.nhs.ctp.service.report.decorators.organisation.mapping;

import java.util.Map;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorators.mapping.AbstractMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization.StandardIndustryClassCode;
import uk.nhs.ctp.service.report.org.hl7.v3.CDAOrganizationTypeDisplayName;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization.TemplateId;

@Primary
@Component
public class OrganizationToRepresentedOrganizationMapper 
		extends AbstractMapper<COCDTP145203GB03Organization, Organization> {

	@Autowired
	private OrganizationToONMapper organizationToONMapper;
	
	@Autowired
	private Map<String, CDAOrganizationTypeDisplayName> organizationTypeMap;
	
	@Override
	public COCDTP145203GB03Organization map(Organization organization) {
		COCDTP145203GB03Organization representedOrganization = new COCDTP145203GB03Organization();
		representedOrganization.setClassCode(representedOrganization.getClassCode());
		representedOrganization.setDeterminerCode(representedOrganization.getDeterminerCode());
		representedOrganization.setName(organizationToONMapper.map(organization));
		
		CDAOrganizationTypeDisplayName code = CDAOrganizationTypeDisplayName.NOT_SPECIFIED;
		if (!organization.getType().isEmpty()) {
			Coding organizationType = organization.getType().get(0).getCodingFirstRep();
			String organizationTypeKey = organizationType.getSystem() + organizationType.getCode();
			if (organizationTypeMap.containsKey(organizationTypeKey)) 
					code = organizationTypeMap.get(organizationTypeKey);
		}
		
		StandardIndustryClassCode classCode = new StandardIndustryClassCode();
		classCode.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.191");
		classCode.setCode(code.value());
		representedOrganization.setStandardIndustryClassCode(classCode);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145200GB01#representedOrganization");
		representedOrganization.setTemplateId(templateId);
		
		return representedOrganization;
	}
		
}
