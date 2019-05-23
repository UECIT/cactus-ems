package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.HumanName;
import org.springframework.beans.factory.annotation.Autowired;

import uk.nhs.ctp.service.report.org.hl7.v3.Entity;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;

public abstract class HumanNameToPersonMapper<PERSON extends Entity<PN>> extends AbstractMapper<PERSON, HumanName> {

	@Autowired 
	private HumanNameToPNMapper humanNameToPNMapper;
	
	public PERSON map(HumanName name) {
		PERSON person = createPerson();
		person.setClassCode(person.getClassCode());
		person.setDeterminerCode(person.getDeterminerCode());
		person.setName(humanNameToPNMapper.map(name));
		
		addTemplateId(person);
		
		return person;
	}
	
	protected abstract PERSON createPerson();
	
	protected abstract void addTemplateId(PERSON person);
}
