package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Person;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Person.TemplateId;

@Component
public class HumanNameToCOCDTP145212GB02PersonMapper extends HumanNameToPersonMapper<COCDTP145212GB02Person> {

	@Override
	protected COCDTP145212GB02Person createPerson() {
		return new COCDTP145212GB02Person();
	}

	@Override
	protected void addTemplateId(COCDTP145212GB02Person person) {
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145200GB01#assignedPerson");
		
		person.setTemplateId(templateId);
	}
}
