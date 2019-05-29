package uk.nhs.ctp.service.report.decorator.mapping.template;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;

import resources.CareConnectRelatedPerson;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.decorator.mapping.RelatedPersonToPersonMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145007UK03Person;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145007UK03RelatedEntity;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145007UK03RelatedEntity.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.RelatedEntityAware;

public abstract class RelatedPersonToRelatedEntityTemplateMapper<CONTAINER extends RelatedEntityAware>
		implements TemplateMapper<CareConnectRelatedPerson, CONTAINER> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Autowired
	private RelatedPersonToPersonMapper relatedPersonToPersonMapper;
	
	@Override
	public void map(CareConnectRelatedPerson relatedPerson, CONTAINER container, ReportRequestDTO request) {
		COCDTP145007UK03RelatedEntity relatedEntity = new COCDTP145007UK03RelatedEntity();
		relatedEntity.setClassCode(relatedEntity.getClassCode());
		relatedEntity.setAddr(addressToADMapper.map(relatedPerson.getAddressFirstRep()));
		relatedEntity.setTelecom(contactPointToTELMapper.map(relatedPerson.getTelecomFirstRep()));
		
		CV cv = new CV();
		cv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.45");
		cv.setCode("98");
		cv.setDisplayName("Not Known");
		relatedEntity.setCode(cv);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		relatedEntity.setTemplateId(templateId);
		
		relatedEntity.setRelationshipHolder(new JAXBElement<COCDTP145007UK03Person>(
				new QName("urn:hl7-org:v3", "relationshipHolder"), COCDTP145007UK03Person.class, 
						relatedPersonToPersonMapper.map(relatedPerson)));
		
		container.setCOCDTP145007UK03RelatedEntity(relatedEntity);
	}

	@Override
	public Class<CareConnectRelatedPerson> getResourceClass() {
		return CareConnectRelatedPerson.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145007UK03#RelatedEntity";
	}
}
