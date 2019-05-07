package uk.nhs.ctp.service.report.decorators;

import org.springframework.stereotype.Component;

import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145202GB02IntendedRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145202GB02Person;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02PrimaryInformationRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class PrimaryInformationRecipientDocumentDecorator implements OneOneOneDecorator, AmbulanceDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.getInformationRecipient().add(createPrimaryInformationRecipient(request));
	}

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		document.getInformationRecipient().add(createPrimaryInformationRecipient(request));
	}
	
	private POCDMT200001GB02PrimaryInformationRecipient createPrimaryInformationRecipient(ReportRequestDTO request) {
		POCDMT200001GB02PrimaryInformationRecipient primaryInformationRecipient = new POCDMT200001GB02PrimaryInformationRecipient();
		primaryInformationRecipient.setTypeCode(primaryInformationRecipient.getTypeCode());
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145202GB02#IntendedRecipient");
		primaryInformationRecipient.setContentId(templateContent);
		
		// set IntendedRecipient
		COCDTP145202GB02IntendedRecipient intendedRecipient = new COCDTP145202GB02IntendedRecipient();
		intendedRecipient.setClassCode(intendedRecipient.getClassCode());
		
		// build assignedPerson
		COCDTP145202GB02Person assignedPerson = new COCDTP145202GB02Person();
		assignedPerson.setClassCode(assignedPerson.getClassCode());
		assignedPerson.setDeterminerCode(assignedPerson.getDeterminerCode());
		CareConnectPractitioner fhirPractitioner = 
				ResourceProviderUtils.getResource(request.getBundle(), CareConnectPractitioner.class);
				
		PN practitionerName = new PN();
		practitionerName.getContent().add(fhirPractitioner.getNameFirstRep().getNameAsSingleString());
		practitionerName.getUse().add(CsEntityNameUse.L);
		assignedPerson.setName(practitionerName);
				
		intendedRecipient.setAssignedPerson(assignedPerson);
		primaryInformationRecipient.setCOCDTP145202GB02IntendedRecipient(intendedRecipient);
		
		return primaryInformationRecipient;
	}

}
