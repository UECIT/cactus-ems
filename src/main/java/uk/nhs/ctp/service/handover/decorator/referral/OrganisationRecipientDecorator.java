package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;

@Component
public class OrganisationRecipientDecorator implements ResourceDecorator<ReferralRequest> {

	public void decorate(ReferralRequest referralRequest) {
		Organization recipientOrganization = new Organization();
		recipientOrganization.setActive(true);
		
		CodeableConcept organizationType = new CodeableConcept();
		organizationType.getCodingFirstRep().setSystem("http://hl7.org/fhir/organization-type");
		organizationType.getCodingFirstRep().setCode("prov");
		organizationType.getCodingFirstRep().setDisplay("Healthcare Provider");
		recipientOrganization.addType(organizationType);
		recipientOrganization.setName("Durham Emergency Department");
		
		// TODO add telecom
		ContactPoint organizationTelecom = new ContactPoint();
		organizationTelecom.setSystem(ContactPointSystem.PHONE);
		organizationTelecom.setValue("0123 456 7899");
		organizationTelecom.setUse(ContactPointUse.HOME);
		organizationTelecom.setRank(0);
		recipientOrganization.addTelecom(organizationTelecom);
		
		// add address
		Address organizationAddress = new Address();
		organizationAddress.addLine("Durham Emergency Department");
		organizationAddress.addLine("Sunderland Rd");
		organizationAddress.setCity("Durham");
		organizationAddress.setPostalCode("S12 2L1");
		recipientOrganization.addAddress(organizationAddress);
		recipientOrganization.setId("#recipientOrganization");
		
		referralRequest.addRecipient().setReference("#recipientOrganization");
		referralRequest.addContained(recipientOrganization);
	}
}
