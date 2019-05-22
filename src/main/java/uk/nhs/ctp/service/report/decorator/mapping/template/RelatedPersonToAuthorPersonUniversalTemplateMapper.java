package uk.nhs.ctp.service.report.decorator.mapping.template;

import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.CodingToCVNPfITCodedplainRequiredMapper;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToCOCDTP145200GB01PersonMapper;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToCOCDTP145203GB03OrganizationMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145200GB01AssignedAuthor.TemplateId;
import uk.nhs.ctp.utils.ResourceProviderUtils;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;

@Component
public class RelatedPersonToAuthorPersonUniversalTemplateMapper implements TemplateMapper<RelatedPerson, POCDMT200001GB02Author> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private CodingToCVNPfITCodedplainRequiredMapper codingMapper;
	
	@Autowired
	private OrganizationToCOCDTP145203GB03OrganizationMapper organizationToRepresentedOrganizationMapper;
	
	@Autowired
	private HumanNameToCOCDTP145200GB01PersonMapper humanNameToAssignedPersonMapper;
	
	@Override
	public void map(RelatedPerson relatedPerson, POCDMT200001GB02Author author, ReportRequestDTO request) {
		CareConnectOrganization organization = ResourceProviderUtils.getResource(
				request.getReferralRequest().getRequester().getOnBehalfOf().getResource(), CareConnectOrganization.class);
		
		COCDTP145200GB01AssignedAuthor assignedAuthor = new COCDTP145200GB01AssignedAuthor();
		assignedAuthor.setClassCode(assignedAuthor.getClassCode());
		assignedAuthor.getAddr().add(addressToADMapper.map(relatedPerson.getAddressFirstRep()));
		assignedAuthor.setCode(codingMapper.map(relatedPerson.getRelationship().getCodingFirstRep()));
		
		assignedAuthor.setAssignedPerson(humanNameToAssignedPersonMapper.map(relatedPerson.getNameFirstRep()));
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
