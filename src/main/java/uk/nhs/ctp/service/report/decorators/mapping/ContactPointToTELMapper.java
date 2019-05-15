package uk.nhs.ctp.service.report.decorators.mapping;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.CsTelecommunicationAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.TEL;

@Component
public class ContactPointToTELMapper extends AbstractMapper<TEL, ContactPoint>{

	private Map<ContactPointUse, CsTelecommunicationAddressUse> 
			contactPointUseToTelecommunicationAddressUseMap = new HashMap<>();
	
	private Map<ContactPointSystem, String> 
			contactPointSystemToTelecommunicationPrefixMap = new HashMap<>();
	
	public ContactPointToTELMapper() {
		contactPointUseToTelecommunicationAddressUseMap.put(
				ContactPointUse.HOME, CsTelecommunicationAddressUse.H);
		
		contactPointSystemToTelecommunicationPrefixMap.put(ContactPointSystem.EMAIL, "mailto:");
		contactPointSystemToTelecommunicationPrefixMap.put(ContactPointSystem.PHONE, "tel:");
		contactPointSystemToTelecommunicationPrefixMap.put(ContactPointSystem.FAX, "fax:");
	}
	
	@Override
	public TEL map(ContactPoint contactPoint) {
		TEL tel = new TEL();

		tel.getUse().add(contactPointUseToTelecommunicationAddressUseMap.get(contactPoint.getUse()));
		tel.setValue(contactPointSystemToTelecommunicationPrefixMap.get(contactPoint.getSystem()) + contactPoint.getValue());

		return tel;
	}

}
