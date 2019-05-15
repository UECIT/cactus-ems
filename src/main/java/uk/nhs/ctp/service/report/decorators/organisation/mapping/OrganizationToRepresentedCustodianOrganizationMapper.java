package uk.nhs.ctp.service.report.decorators.organisation.mapping;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorators.mapping.AbstractMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03CustodianOrganization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03CustodianOrganization.TemplateId;

@Component
public class OrganizationToRepresentedCustodianOrganizationMapper 
		extends AbstractMapper<COCDTP145018UK03CustodianOrganization, Organization> {
	
	@Autowired
	private OrganizationToONMapper stringToONMapper;

	@Override
	public COCDTP145018UK03CustodianOrganization map(Organization organization) {
		COCDTP145018UK03CustodianOrganization custodianOrganization = new COCDTP145018UK03CustodianOrganization();
		custodianOrganization.setClassCode(custodianOrganization.getClassCode());
		custodianOrganization.setDeterminerCode(custodianOrganization.getDeterminerCode());
		custodianOrganization.setName(stringToONMapper.map(organization));
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145200GB01#representedCustodianOrganization");
		custodianOrganization.setTemplateId(templateId);
		
		return custodianOrganization;
	}
}
