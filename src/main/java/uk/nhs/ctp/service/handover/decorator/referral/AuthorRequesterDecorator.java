package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestRequesterComponent;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;

@Component
public class AuthorRequesterDecorator implements ResourceDecorator<ReferralRequest, AuditEntry> {

	public void decorate(ReferralRequest referralRequest, AuditEntry auditEntry) {
		Address address = new Address()
			.addLine("111 Handler Street")
			.addLine("Westgate")
			.setCity("Leeds")
			.setPostalCode("LS1 1SL")
			.setType(AddressType.PHYSICAL);
		
		CodeableConcept relationship = new CodeableConcept().addCoding(new Coding()
			.setSystem("https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-SDSJobRoleName-1")
			.setCode("R1690")
			.setDisplay("Call Operator"));
		
		CodeableConcept organizationType = new CodeableConcept().addCoding(new Coding()
			.setSystem("http://terminology.hl7.org/CodeSystem/organization-type")
			.setCode("prov")
			.setDisplay("Healthcare Provider"));
	
		RelatedPerson author = new RelatedPerson();
		
		author.addAddress(address);
		author.addIdentifier(new Identifier().setType(relationship));
		author.addTelecom().setValue("0800 000 0000");
		author.addName(new HumanName()
				.setFamily("Smith").addGiven("Jane").addPrefix("Ms").setUse(NameUse.OFFICIAL));
	
		author.setActive(true);
		author.setGender(AdministrativeGender.FEMALE);
		author.setId("#agent");
		author.setRelationship(relationship);
		
		CareConnectOrganization onBehalfOf = new CareConnectOrganization();
		
		onBehalfOf.addAddress(address);
		onBehalfOf.addTelecom().setValue("0800 000 0000");
		onBehalfOf.addType(organizationType);
		
		onBehalfOf.setActive(true);
		onBehalfOf.setId("#onBehalfOf");
		onBehalfOf.setName("111 Centre");
		
		ReferralRequestRequesterComponent requester = new ReferralRequestRequesterComponent();
		requester.setAgent(new Reference(author));
		requester.setOnBehalfOf(new Reference(onBehalfOf));
		
		referralRequest.setRequester(requester);
	}
}

	
	//new CodeableConcept().addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0131").setCode("S").setDisplay("State Agency"))));