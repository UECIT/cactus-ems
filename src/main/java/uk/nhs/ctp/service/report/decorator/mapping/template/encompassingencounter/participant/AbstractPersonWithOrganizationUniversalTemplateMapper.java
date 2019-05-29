package uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.participant;

import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import resources.CareConnectOrganization;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToCOCDTP145210GB01PersonMapper;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToCOCDTP145210GB01OrganizationMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.AssignedEntityAware;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;

public abstract class AbstractPersonWithOrganizationUniversalTemplateMapper
		<RESOURCE extends Resource, CONTAINER extends AssignedEntityAware> 
				implements TemplateMapper<RESOURCE, CONTAINER> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Autowired
	private HumanNameToCOCDTP145210GB01PersonMapper humanNameToPersonMapper;
	
	@Autowired
	private OrganizationToCOCDTP145210GB01OrganizationMapper organizationToProviderOrganizationMapper;
	
	@Override
	public void map(RESOURCE resource, CONTAINER container, ReportRequestDTO request) {
		
		COCDTP145210GB01AssignedEntity assignedEntity = new COCDTP145210GB01AssignedEntity();
		assignedEntity.setClassCode(assignedEntity.getClassCode());
		assignedEntity.setAddr(addressToADMapper.map(getAddress(resource)));
		assignedEntity.getTelecom().addAll(contactPointToTELMapper.map(getTelecom(resource)));
		assignedEntity.setAssignedPerson(humanNameToPersonMapper.map(getName(resource)));
		
		assignedEntity.setRepresentedOrganization(
				organizationToProviderOrganizationMapper.map(getOrganization(resource)));
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145210GB01#AssignedEntity");
		assignedEntity.setTemplateId(templateId);
		
		IINPfITOidRequiredAssigningAuthorityName assigningAuthorityNameId = new IINPfITOidRequiredAssigningAuthorityName();
		assigningAuthorityNameId.setNullFlavor(CsNullFlavor.NA);
		assignedEntity.getId().add(assigningAuthorityNameId);
		
		container.setCOCDTP145210GB01AssignedEntity(assignedEntity);
	}

	protected abstract HumanName getName(RESOURCE resource);

	protected abstract List<ContactPoint> getTelecom(RESOURCE resource);

	protected abstract CareConnectOrganization getOrganization(RESOURCE resource);

	protected abstract Address getAddress(RESOURCE resource);
}
