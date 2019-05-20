package uk.nhs.ctp.service.report.decorator.mapping.template;

import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.CodingToCVNPfITCodedplainRequiredMapper;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToPNMapper;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToRepresentedOrganizationMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01Person;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;

@Component
public class RelatedPersonToAuthorPersonUniversalTemplateMapper implements TemplateMapper<RelatedPerson, POCDMT200001GB02Author> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private CodingToCVNPfITCodedplainRequiredMapper codingMapper;
	
	@Autowired
	private OrganizationToRepresentedOrganizationMapper organizationToRepresentedOrganizationMapper;
	
	@Autowired 
	private HumanNameToPNMapper humanNameToPNMapper;
	
	@Override
	public void map(RelatedPerson relatedPerson, POCDMT200001GB02Author author, ReportRequestDTO request) {
		CareConnectOrganization organization = 
				(CareConnectOrganization)request.getReferralRequest().getRequester().getOnBehalfOf().getResource();
		
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
		assignedAuthorTemplate.setExtension(getTemplateName());
		assignedAuthor.setTemplateId(assignedAuthorTemplate);

		author.setCOCDTP145200GB01AssignedAuthor(assignedAuthor);
	}

	@Override
	public Class<RelatedPerson> getResourceClass() {
		return RelatedPerson.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145200GB01#AssignedAuthor";
	}

}
