package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01Person;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01Person.TemplateId;

@Component
public class HumanNameToCOCDTP145210GB01PersonMapper extends HumanNameToPersonMapper<COCDTP145210GB01Person> {

	@Override
	protected COCDTP145210GB01Person createPerson() {
		return new COCDTP145210GB01Person();
	}

	@Override
	protected void addTemplateId(COCDTP145210GB01Person person) {
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145210GB01#assignedPerson");
		
		person.setTemplateId(templateId);
	}

}
