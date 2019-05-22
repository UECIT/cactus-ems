package uk.nhs.ctp.service.report.decorator;

import java.util.List;

import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToCOCDTP145202GB02PersonMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.AD;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145202GB02IntendedRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03IntendedRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.CsPostalAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.CsTelecommunicationAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02PrimaryInformationRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.TEL;

@Component
public class InformationRecipientDecorator implements OneOneOneDecorator, AmbulanceDecorator {

	@Autowired
	private HumanNameToCOCDTP145202GB02PersonMapper humanNameToPersonMapper;
	
	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		List<POCDMT200001GB02PrimaryInformationRecipient> informationRecipients = document.getInformationRecipient();
		createInformationRecipients(informationRecipients, request);
	}

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		List<POCDMT200001GB02PrimaryInformationRecipient> informationRecipients = document.getInformationRecipient();
		createInformationRecipients(informationRecipients, request);
	}
	
	private List<POCDMT200001GB02PrimaryInformationRecipient> createInformationRecipients(List<POCDMT200001GB02PrimaryInformationRecipient> informationRecipients, ReportRequestDTO request) {
		for (Reference informationRecipient : request.getReferralRequest().getRecipient()) {
			if(informationRecipient.getResource() instanceof CareConnectOrganization) {
				informationRecipients.add(createInformationRecipient((CareConnectOrganization)informationRecipient.getResource()));
			}
			if(informationRecipient.getResource() instanceof CareConnectPractitioner) {
				informationRecipients.add(createInformationRecipient((CareConnectPractitioner)informationRecipient.getResource()));
			}
		}
		return informationRecipients;
	}
	
	private POCDMT200001GB02PrimaryInformationRecipient createInformationRecipient(CareConnectPractitioner informationRecipient) {
		POCDMT200001GB02PrimaryInformationRecipient primaryInformationRecipient = buildGenericInformationRecipient();
		
		COCDTP145202GB02IntendedRecipient intendedRecipientPractitioner = new COCDTP145202GB02IntendedRecipient();
		intendedRecipientPractitioner.setClassCode(intendedRecipientPractitioner.getClassCode());
		
		AD practitionerAddress = new AD();
		practitionerAddress.getContent().add(informationRecipient.getAddressFirstRep().getLine().get(0).getValue());
		practitionerAddress.getContent().add(informationRecipient.getAddressFirstRep().getLine().get(1).getValue());
		practitionerAddress.getContent().add(informationRecipient.getAddressFirstRep().getCity());
		practitionerAddress.getContent().add(informationRecipient.getAddressFirstRep().getPostalCode());
		practitionerAddress.getUse().add(CsPostalAddressUse.PHYS);
		intendedRecipientPractitioner.setAddr(practitionerAddress);
		
		IINPfITOidRequiredAssigningAuthorityName id = new IINPfITOidRequiredAssigningAuthorityName();
		id.setNullFlavor(CsNullFlavor.NA);
		intendedRecipientPractitioner.getId().add(id);
		
		CV recipientRoleCode = new CV();
		recipientRoleCode.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.124");
		recipientRoleCode.setCode("NR0270");
		recipientRoleCode.setDisplayName("Salaried General Practitioner");
		intendedRecipientPractitioner.setRecipientRoleCode(recipientRoleCode);
		
		TEL phone = new TEL();
		phone.setValue(informationRecipient.getTelecomFirstRep().getValue());
		phone.getUse().add(CsTelecommunicationAddressUse.H);
		intendedRecipientPractitioner.getTelecom().add(phone);
		
		COCDTP145202GB02IntendedRecipient.TemplateId templateId = new COCDTP145202GB02IntendedRecipient.TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145203GB03#representedOrganization");
		intendedRecipientPractitioner.setTemplateId(templateId);
		
		intendedRecipientPractitioner.setAssignedPerson(
				humanNameToPersonMapper.map(informationRecipient.getNameFirstRep()));
		
		primaryInformationRecipient.setCOCDTP145202GB02IntendedRecipient(intendedRecipientPractitioner);
		
		return primaryInformationRecipient;
	}
	
	private POCDMT200001GB02PrimaryInformationRecipient createInformationRecipient(CareConnectOrganization informationRecipient) {
		POCDMT200001GB02PrimaryInformationRecipient primaryInformationRecipient = buildGenericInformationRecipient();
		
		COCDTP145203GB03IntendedRecipient intendedRecipientOrganization = new COCDTP145203GB03IntendedRecipient();
		intendedRecipientOrganization.setClassCode(intendedRecipientOrganization.getClassCode());
		
		AD organizationAddress = new AD();
		organizationAddress.getContent().add(informationRecipient.getAddressFirstRep().getLine().get(0).getValue());
		organizationAddress.getContent().add(informationRecipient.getAddressFirstRep().getLine().get(1).getValue());
		organizationAddress.getContent().add(informationRecipient.getAddressFirstRep().getCity());
		organizationAddress.getContent().add(informationRecipient.getAddressFirstRep().getPostalCode());
		organizationAddress.getUse().add(CsPostalAddressUse.PHYS);
		
		intendedRecipientOrganization.setAddr(organizationAddress);
		
		TEL phone = new TEL();
		phone.setValue(informationRecipient.getTelecomFirstRep().getValue());
		phone.getUse().add(CsTelecommunicationAddressUse.H);
		intendedRecipientOrganization.getTelecom().add(phone);
		
		COCDTP145203GB03IntendedRecipient.TemplateId intendedRecipientTemplateId = new COCDTP145203GB03IntendedRecipient.TemplateId();
		intendedRecipientTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		intendedRecipientTemplateId.setExtension("COCD_TP145203GB03#IntendedRecipient");
		intendedRecipientOrganization.setTemplateId(intendedRecipientTemplateId);
		
		// build representedOrganization
		COCDTP145203GB03Organization representedOrganization = new COCDTP145203GB03Organization();
		representedOrganization.setClassCode(representedOrganization.getClassCode());
		representedOrganization.setDeterminerCode(representedOrganization.getDeterminerCode());
		
		COCDTP145203GB03Organization.Id id = new COCDTP145203GB03Organization.Id();
		id.setNullFlavor(CsNullFlavor.NA);
		representedOrganization.setId(id);
		
		COCDTP145203GB03Organization.TemplateId templateId = new COCDTP145203GB03Organization.TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145203GB03#representedOrganization");
		representedOrganization.setTemplateId(templateId);
		
		ON organizationName = new ON();
		organizationName.getContent().add(informationRecipient.getName());
		organizationName.getUse().add(CsEntityNameUse.L);
		representedOrganization.setName(organizationName);
		
		COCDTP145203GB03Organization.StandardIndustryClassCode standardIndustryClassCode = new COCDTP145203GB03Organization.StandardIndustryClassCode();
		standardIndustryClassCode.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.191");
		standardIndustryClassCode.setDisplayName("General Medical Practice");
		standardIndustryClassCode.setCode("001");
		representedOrganization.setStandardIndustryClassCode(standardIndustryClassCode);
		
		intendedRecipientOrganization.setRepresentedOrganization(representedOrganization);
		primaryInformationRecipient.setCOCDTP145203GB03IntendedRecipient(intendedRecipientOrganization);
		
		return primaryInformationRecipient;
	}
	
	private POCDMT200001GB02PrimaryInformationRecipient buildGenericInformationRecipient() {
		POCDMT200001GB02PrimaryInformationRecipient primaryInformationRecipient = new POCDMT200001GB02PrimaryInformationRecipient();
		primaryInformationRecipient.setTypeCode(primaryInformationRecipient.getTypeCode());
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145202GB02#IntendedRecipient");
		primaryInformationRecipient.setContentId(templateContent);
		
		return primaryInformationRecipient;
	}

}
