package uk.nhs.ctp.service.report.decorators.organisation.mapping;

import java.util.Map;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorators.mapping.AbstractMapper;
import uk.nhs.ctp.service.report.decorators.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorators.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Organization.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization.StandardIndustryClassCode;
import uk.nhs.ctp.service.report.org.hl7.v3.CDAOrganizationTypeDisplayName;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;

@Component
public class OrganizationToProviderOrganizationMapper
		extends AbstractMapper<COCDTP145201GB01Organization, Organization> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Autowired
	private OrganizationToONMapper stringToONMapper;
	
	@Autowired
	private Map<String, CDAOrganizationTypeDisplayName> organizationTypeMap;
	
	@Override
	public COCDTP145201GB01Organization map(Organization organization) {
		COCDTP145201GB01Organization providerOrganization = new COCDTP145201GB01Organization();
		providerOrganization.setClassCode(providerOrganization.getClassCode());
		providerOrganization.setDeterminerCode(providerOrganization.getDeterminerCode());
		providerOrganization.setName(stringToONMapper.map(organization));
		providerOrganization.setAddr(addressToADMapper.map(organization.getAddressFirstRep()));
		providerOrganization.getTelecom().addAll(contactPointToTELMapper.map(organization.getTelecom()));
		
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
		providerOrganization.setStandardIndustryClassCode(classCode);
		
		CV cv = new CV();
		cv.setCode("001");
		cv.setDisplayName("GP Practice");
		providerOrganization.setStandardIndustryClassCode(cv);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145201GB01#providerOrganization");
		providerOrganization.setTemplateId(templateId);
		
		return providerOrganization;
	}
}
