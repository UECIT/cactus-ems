package uk.nhs.ctp.service.report.decorators.author.mapping;

import java.util.function.BiConsumer;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorators.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorators.mapping.CodingToCVNPfITCodedplainRequiredMapper;
import uk.nhs.ctp.service.report.decorators.mapping.HumanNameToPNMapper;
import uk.nhs.ctp.service.report.decorators.mapping.StringToONMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Organization.Id;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;

@Component
public class RelatedPersonToAssignedAuthorDataMapper implements AuthorDataMapper<COCDTP145200GB01AssignedAuthor, RelatedPerson> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private CodingToCVNPfITCodedplainRequiredMapper codeableConceptMapper;
	
	@Autowired 
	HumanNameToPNMapper humanNameToPNMapper;
	
	@Autowired
	StringToONMapper stringToONMapper;
	
	@Override
	public COCDTP145200GB01AssignedAuthor map(RelatedPerson relatedPerson, Organization organization) {
		// Set the assignedAuthor
		COCDTP145200GB01AssignedAuthor assignedAuthor = new COCDTP145200GB01AssignedAuthor();
		assignedAuthor.setClassCode(assignedAuthor.getClassCode());
		// set author Address
		assignedAuthor.getAddr().add(addressToADMapper.map(relatedPerson.getAddressFirstRep()));
		assignedAuthor.setCode(codeableConceptMapper.map(relatedPerson.getRelationship().getCodingFirstRep()));
		
		// set AssignedPerson
		COCDTP145200GB01Person assignedPerson = new COCDTP145200GB01Person();
		assignedPerson.setClassCode(assignedPerson.getClassCode());
		assignedPerson.setDeterminerCode(assignedPerson.getDeterminerCode());

		assignedPerson.setName(humanNameToPNMapper.map(relatedPerson.getNameFirstRep()));

		uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person.TemplateId assignedPersonTemplate = new uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person.TemplateId();
		assignedPersonTemplate.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		assignedPersonTemplate.setExtension("COCD_TP145200GB01#assignedPerson");
		assignedPerson.setTemplateId(assignedPersonTemplate);
		
		assignedAuthor.setAssignedPerson(assignedPerson);
	
		COCDTP145200GB01Organization representedOrganization = new COCDTP145200GB01Organization();
		representedOrganization.setClassCode(representedOrganization.getClassCode());
		representedOrganization.setDeterminerCode(representedOrganization.getDeterminerCode());
		// set organization ODS code
		Id odsId = new Id();
		odsId.setRoot("2.16.840.1.113883.2.1.3.2.4.19.2 ");
		odsId.setExtension("EMS01");
		representedOrganization.setId(odsId);
		// set organization name
	
		
		representedOrganization.setName(stringToONMapper.map(organization.getName()));
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
		
		return assignedAuthor;
	}

	@Override
	public Class<RelatedPerson> getResourceClass() {
		return RelatedPerson.class;
	}

	@Override
	public BiConsumer<POCDMT200001GB02Author, Object> mappingFunction() {
		return (author, assignedAuthor) -> 
				author.setCOCDTP145200GB01AssignedAuthor((COCDTP145200GB01AssignedAuthor)assignedAuthor);
	}

}
