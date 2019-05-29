package uk.nhs.ctp.service.handover.decorator.referral;

import java.util.Date;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import resources.CareConnectPractitioner;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;

@Component
public class PractitionerRecipientDecorator implements ResourceDecorator<ReferralRequest, AuditEntry> {

	public void decorate(ReferralRequest referralRequest, AuditEntry auditEntry) {
		CareConnectPractitioner recipientPractitioner = new CareConnectPractitioner();
		HumanName name = new HumanName();
		name.addSuffix("Dr");
		name.addGiven("John");
		name.setFamily("Blog");
		recipientPractitioner.getName().add(name);
		
		recipientPractitioner.setGender(AdministrativeGender.MALE);
		recipientPractitioner.setBirthDate(new Date());
		
		Address gpAddress = new Address();
		gpAddress.addLine("Durham Emergency Department");
		gpAddress.addLine("Sunderland Rd");
		gpAddress.setCity("Durham");
		gpAddress.setPostalCode("S12 2L1");
		gpAddress.setUse(AddressUse.WORK);
		recipientPractitioner.addAddress(gpAddress);
		
		CodeableConcept practitionerQualification = new CodeableConcept();
		practitionerQualification.addCoding();
		practitionerQualification.getCodingFirstRep().setSystem(SystemURL.SNOMED);
		practitionerQualification.getCodingFirstRep().setCode("62247001");
		practitionerQualification.getCodingFirstRep().setDisplay("GP - General practitioner");
		recipientPractitioner.addQualification(new PractitionerQualificationComponent(practitionerQualification));
		recipientPractitioner.setId("#recipientPractitioner");
		
		referralRequest.addRecipient().setReference("#recipientPractitioner");
		referralRequest.addContained(recipientPractitioner);
	}
}
