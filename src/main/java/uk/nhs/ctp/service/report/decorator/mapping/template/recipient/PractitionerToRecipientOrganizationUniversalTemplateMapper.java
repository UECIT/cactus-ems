package uk.nhs.ctp.service.report.decorator.mapping.template.recipient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToCOCDTP145202GB02PersonMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145202GB02IntendedRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145202GB02IntendedRecipient.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;
import uk.nhs.ctp.service.report.org.hl7.v3.RecipientAware;

@Component
public class PractitionerToRecipientOrganizationUniversalTemplateMapper<CONTAINER extends RecipientAware>
		implements TemplateMapper<CareConnectPractitioner, CONTAINER> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Autowired
	private HumanNameToCOCDTP145202GB02PersonMapper humanNameToPersonMapper;
	
	@Override
	public Class<CareConnectPractitioner> getResourceClass() {
		return CareConnectPractitioner.class;
	}

	@Override
	public void map(CareConnectPractitioner practitioner, CONTAINER container, ReportRequestDTO request) {
		
		COCDTP145202GB02IntendedRecipient intendedRecipient = new COCDTP145202GB02IntendedRecipient();
		intendedRecipient.setClassCode(intendedRecipient.getClassCode());
		intendedRecipient.setAddr(addressToADMapper.map(practitioner.getAddressFirstRep()));
		intendedRecipient.getTelecom().addAll(contactPointToTELMapper.map(practitioner.getTelecom()));
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		intendedRecipient.setTemplateId(templateId);
		
		IINPfITOidRequiredAssigningAuthorityName id = new IINPfITOidRequiredAssigningAuthorityName();
		id.setNullFlavor(CsNullFlavor.NA);
		intendedRecipient.getId().add(id);
		
		CV recipientRoleCode = new CV();
		recipientRoleCode.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.124");
		recipientRoleCode.setCode("NR0270");
		recipientRoleCode.setDisplayName("Salaried General Practitioner");
		intendedRecipient.setRecipientRoleCode(recipientRoleCode);
		intendedRecipient.setAssignedPerson(humanNameToPersonMapper.map(practitioner.getNameFirstRep()));
		container.setCOCDTP145202GB02IntendedRecipient(intendedRecipient);
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145203GB03#IntendedRecipient";
	}
}
