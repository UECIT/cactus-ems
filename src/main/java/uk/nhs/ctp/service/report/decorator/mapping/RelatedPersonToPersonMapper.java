package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.CareConnectRelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145007UK03Person;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145007UK03Person.TemplateId;

@Component
public class RelatedPersonToPersonMapper extends AbstractMapper<COCDTP145007UK03Person, CareConnectRelatedPerson>{

	@Autowired
	private HumanNameToPNMapper humanNameToPNMapper;
	
	@Override
	public COCDTP145007UK03Person map(CareConnectRelatedPerson relatedPerson) {
		COCDTP145007UK03Person person = new COCDTP145007UK03Person();
		person.setClassCode(person.getClassCode());
		person.setDeterminerCode(person.getDeterminerCode());
		person.setName(humanNameToPNMapper.map(relatedPerson.getNameFirstRep()));
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145007UK03#relationshipHolder");
		person.setTemplateId(templateId);
		
		return person;
	}

}
