package uk.nhs.ctp.service.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.NHSNumberIdentifier;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.service.ReferenceStorageService;

@Component
public class CareConnectPatientBuilder {

	private CareConnectOrganizationBuilder careConnectOrganizationBuilder;
	private CareConnectPractitionerBuilder careConnectPractitionerBuilder;

	public CareConnectPatientBuilder(
			CareConnectOrganizationBuilder careConnectOrganizationBuilder,
			CareConnectPractitionerBuilder careConnectPractitionerBuilder) {
		this.careConnectOrganizationBuilder = careConnectOrganizationBuilder;
		this.careConnectPractitionerBuilder = careConnectPractitionerBuilder;
	}

	public CareConnectPatient build(Cases caseEntity, ReferenceStorageService storageService) {
		NHSNumberIdentifier nhsIdentifier = new NHSNumberIdentifier();
		nhsIdentifier.setValue("4323543455");
		nhsIdentifier.setSystem("https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-NHSNumberVerificationStatus-1");
		nhsIdentifier.setNhsNumberVerificationStatus(new CodeableConcept().addCoding(new Coding()
			.setSystem("https://fhir.hl7.org.uk/STU3/ValueSet/CareConnect-NHSNumberVerificationStatus-1")
			.setDisplay("Number present and verified")
			.setCode("21")));
		nhsIdentifier.setType(new CodeableConcept().addCoding(new Coding()
			.setSystem("http://hl7.org/fhir/ValueSet/identifier-type")
			.setDisplay("Passport number")
			.setCode("PPN")));
	
		var names = new ArrayList<HumanName>();
		names.add(new HumanName()
				.setFamily(caseEntity.getLastName())
				.addGiven(caseEntity.getFirstName())
				.setUse(NameUse.OFFICIAL));

		CodeableConcept language = new CodeableConcept();
		language.addCoding().setCode("en").setDisplay("English")
				.setSystem("http://uecdi-tom-terminology.eu-west-2.elasticbeanstalk.com/fhir/CodeSystem/languages");

		CareConnectPatient patient = new CareConnectPatient();
		patient.addIdentifier(nhsIdentifier);
		patient.setName(names);
		patient.setGender(Enumerations.AdministrativeGender.fromCode(caseEntity.getGender()));
		patient.setBirthDate(caseEntity.getDateOfBirth());
		patient.addCommunication(new PatientCommunicationComponent(language));
		
		CareConnectOrganization pharmacy = careConnectOrganizationBuilder.build(caseEntity);
		patient.setNominatedPharmacy(storageService.store(pharmacy));

		var practitioner = careConnectPractitionerBuilder.build(pharmacy);
		patient.addGeneralPractitioner(storageService.store(practitioner));
		
		patient.addAddress()
			.setUse(AddressUse.HOME)
			.setType(AddressType.BOTH)
			.addLine("20 example street")
			.addLine("flat 1")
			.setCity("Leeds")
			.setDistrict("Headingly")
			.setPostalCode("LS6 4EX")
			.setCountry("United Kingdom")
			.setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
		
		patient.addTelecom()
			.setSystem(ContactPointSystem.PHONE)
			.setValue("01231231234")
			.setUse(ContactPointUse.HOME)
			.setRank(1)
			.setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
		
		patient.addTelecom()
			.setSystem(ContactPointSystem.PHONE)
			.setValue("01234567899")
			.setUse(ContactPointUse.MOBILE)
			.setRank(2)
			.setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
		
		patient.addTelecom()
			.setSystem(ContactPointSystem.EMAIL)
			.setValue("exmaple@example.com")
			.setUse(ContactPointUse.HOME)
			.setRank(3)
			.setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
		
		return patient;
	}
}
