package uk.nhs.ctp.service.report.decorators;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Organization.Id;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplainRequired;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author.FunctionCode;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author.Time;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;

@Component
public class AuthorDocumentDecorator implements OneOneOneDecorator, AmbulanceDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.getAuthor().add(createAuthor());
	}

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		document.getAuthor().add(createAuthor());
	}
	
	private POCDMT200001GB02Author createAuthor() {
		POCDMT200001GB02Author author  = new POCDMT200001GB02Author();
		// The HL7 attribute typeCode uses a code to describe this class as an author participation.
		author.setTypeCode(author.getTypeCode());
		author.getContextControlCode().add("OP");

		// The HL7 (NHS localisation) attribute contentId, when valued in an instance, provides a unique forward pointing identifier for the template 
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145200GB01#AssignedAuthor");
		author.setContentId(templateContent);

		// The HL7 attribute functionCode uses a code from the vocabulary AuthorFunctionType to describe the function of the author.
		FunctionCode functionCode = new FunctionCode();
		functionCode.setCode("OA");
		functionCode.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.178");
		functionCode.setDisplayName("Originating Author");
		author.setFunctionCode(functionCode);

		// The HL7 attribute author time is used to indicate when the person authored the CDA document.
		Time time = new Time();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		time.setValue(format.format(new Date()));
		author.setTime(time);
		
		// Set the assignedAuthor
		COCDTP145200GB01AssignedAuthor assignedAuthor = new COCDTP145200GB01AssignedAuthor();
		assignedAuthor.setClassCode(assignedAuthor.getClassCode());
		// set author Address
		assignedAuthor.getAddr().add(null);
		// set code representing job role
		CVNPfITCodedplainRequired authorJobRole = new CVNPfITCodedplainRequired();
		authorJobRole.setCode("NR1690");
		authorJobRole.setDisplayName("Call Operator");
		authorJobRole.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.124");
		assignedAuthor.setCode(authorJobRole);
		// set AssignedPerson
		COCDTP145200GB01Person assignedPerson = new COCDTP145200GB01Person();
		assignedPerson.setClassCode(assignedPerson.getClassCode());
		assignedPerson.setDeterminerCode(assignedPerson.getDeterminerCode());
		// set persons name
		PN personsName = new PN();
		personsName.getContent().add("Jim Bob Walton, Jr");
		personsName.getUse().add(CsEntityNameUse.L);
		assignedPerson.setName(personsName);
		// set TemplateId
		uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person.TemplateId assignedPersonTemplate = new uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person.TemplateId();
		assignedPersonTemplate.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		assignedPersonTemplate.setExtension("COCD_TP145200GB01#assignedPerson");
		assignedPerson.setTemplateId(assignedPersonTemplate);
		
		assignedAuthor.setAssignedPerson(assignedPerson);
		// set representedOrganization
		COCDTP145200GB01Organization representedOrganization = new COCDTP145200GB01Organization();
		representedOrganization.setClassCode(representedOrganization.getClassCode());
		representedOrganization.setDeterminerCode(representedOrganization.getDeterminerCode());
		// set organization ODS code
		Id odsId = new Id();
		odsId.setRoot("2.16.840.1.113883.2.1.3.2.4.19.2 ");
		odsId.setExtension("EMS01");
		representedOrganization.setId(odsId);
		// set organization name
		ON organizationName = new ON();
		organizationName.getContent().add("EMS Test Harness");
		organizationName.getUse().add(CsEntityNameUse.L);
		representedOrganization.setName(organizationName);
		// set TemplateId
		uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Organization.TemplateId representedOrganizationTemplate = new uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Organization.TemplateId();
		representedOrganizationTemplate.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		representedOrganizationTemplate.setExtension("COCD_TP145200GB01#representedOrganization");
		representedOrganization.setTemplateId(representedOrganizationTemplate);
		assignedAuthor.setRepresentedOrganization(representedOrganization);
		
		// set templateID
		TemplateId assignedAuthorTemplate = new TemplateId();
		assignedAuthorTemplate.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		assignedAuthorTemplate.setExtension("COCD_TP145200GB01#AssignedAuthor");
		assignedAuthor.setTemplateId(assignedAuthorTemplate);
		author.setCOCDTP145200GB01AssignedAuthor(assignedAuthor);
		
		return author;
	}

}
