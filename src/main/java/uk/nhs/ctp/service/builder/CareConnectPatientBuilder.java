package uk.nhs.ctp.service.builder;

import datatypes.NHSNumberIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import resources.CareConnectPatient;

import uk.nhs.ctp.entities.Cases;

@Component
public class CareConnectPatientBuilder {

	@Autowired
	private CareConnectOrganizationBuilder careConnectOrganizationBuilder;
	
	@Autowired
	private CareConnectPractitionerBuilder careConnectPractitionerBuilder;
	
	public CareConnectPatient build(Cases caseEntity) {
		NHSNumberIdentifier nhsIdentifier = new NHSNumberIdentifier();
		nhsIdentifier.setValue("432354345");
		nhsIdentifier.setSystem("https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-NHSNumberVerificationStatus-1");
		nhsIdentifier.setNhsNumberVerificationStatus(new CodeableConcept().addCoding(new Coding()
			.setSystem("https://fhir.hl7.org.uk/STU3/ValueSet/CareConnect-NHSNumberVerificationStatus-1")
			.setDisplay("Number present and verified")
			.setCode("21")));
		nhsIdentifier.setType(new CodeableConcept().addCoding(new Coding()
			.setSystem("http://hl7.org/fhir/ValueSet/identifier-type")
			.setDisplay("Passport number")
			.setCode("PPN")));
	
		List<HumanName> names = new ArrayList<HumanName>();
		names.add(new HumanName().setFamily(caseEntity.getLastName()).addGiven(caseEntity.getFirstName()));

		CodeableConcept language = new CodeableConcept();
		language.addCoding().setCode("en").setDisplay("English").setSystem("http://uecdi-tom-terminology.eu-west-2.elasticbeanstalk.com/fhir/CodeSystem/languages");

		CareConnectPatient patient = new CareConnectPatient();
		patient.setIdentifier(Arrays.asList(new NHSNumberIdentifier[]{nhsIdentifier}));
		patient.setName(names);
		patient.setGender(Enumerations.AdministrativeGender.fromCode(caseEntity.getGender()));
		patient.setBirthDate(caseEntity.getDateOfBirth());
		patient.addCommunication(new PatientCommunicationComponent(language));
		
		CareConnectOrganization pharmacy = careConnectOrganizationBuilder.build(caseEntity);
		
		patient.setNominatedPharmacy(new Reference(pharmacy));
		patient.addGeneralPractitioner(new Reference(careConnectPractitionerBuilder.build(pharmacy)));
		
		return patient;
	}
}
