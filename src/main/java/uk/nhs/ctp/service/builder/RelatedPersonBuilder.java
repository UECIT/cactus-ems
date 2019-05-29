package uk.nhs.ctp.service.builder;

import java.util.Date;

import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;

import resources.CareConnectRelatedPerson;

@Component
public class RelatedPersonBuilder {

	public CareConnectRelatedPerson build() {
		CareConnectRelatedPerson person = new CareConnectRelatedPerson();
		person.addAddress()
			.setUse(AddressUse.HOME)
			.setType(AddressType.BOTH)
			.addLine("1 Lovely View")
			.addLine("High Street")
			.setCity("Sheffield")
			.setDistrict("South Yorkshire")
			.setPostalCode("S1 4LL")
			.setCountry("United Kingdom")
			.setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
		
		person.addName()
			.addGiven("Charles")
			.addPrefix("Mr")
			.setFamily("Ingram");
		
		person.addTelecom()
			.setSystem(ContactPointSystem.PHONE)
			.setValue("01904134543")
			.setUse(ContactPointUse.HOME)
			.setRank(1)
			.setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
		
		return person;
	}
}
