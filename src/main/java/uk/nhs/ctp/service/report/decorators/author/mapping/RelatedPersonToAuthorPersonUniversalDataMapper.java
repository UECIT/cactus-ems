package uk.nhs.ctp.service.report.decorators.author.mapping;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorators.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorators.mapping.CodingToCVNPfITCodedplainRequiredMapper;
import uk.nhs.ctp.service.report.decorators.mapping.HumanNameToPNMapper;
import uk.nhs.ctp.service.report.decorators.organisation.mapping.OrganizationToRepresentedOrganizationMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;

@Component
public class RelatedPersonToAuthorPersonUniversalDataMapper implements AuthorDataMapper<RelatedPerson> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private CodingToCVNPfITCodedplainRequiredMapper codingMapper;
	
	@Autowired
	private OrganizationToRepresentedOrganizationMapper organizationToRepresentedOrganizationMapper;
	
	@Autowired 
	private HumanNameToPNMapper humanNameToPNMapper;
	
	@Override
	public void map(RelatedPerson relatedPerson, Organization organization, POCDMT200001GB02Author author) {

		COCDTP145200GB01AssignedAuthor assignedAuthor = new COCDTP145200GB01AssignedAuthor();
		assignedAuthor.setClassCode(assignedAuthor.getClassCode());
		assignedAuthor.getAddr().add(addressToADMapper.map(relatedPerson.getAddressFirstRep()));
		assignedAuthor.setCode(codingMapper.map(relatedPerson.getRelationship().getCodingFirstRep()));
		
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
		assignedAuthor.setRepresentedOrganization(organizationToRepresentedOrganizationMapper.map(organization));
		
		// set templateID
		TemplateId assignedAuthorTemplate = new TemplateId();
		assignedAuthorTemplate.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		assignedAuthorTemplate.setExtension("COCD_TP145200GB01#AssignedAuthor");
		assignedAuthor.setTemplateId(assignedAuthorTemplate);

		author.setCOCDTP145200GB01AssignedAuthor(assignedAuthor);
		author.setContentId(getTemplateContent());
	}

	@Override
	public Class<RelatedPerson> getResourceClass() {
		return RelatedPerson.class;
	}
	
	private TemplateContent getTemplateContent() {
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145200GB01#AssignedAuthor");
		
		return templateContent;
	}

}
