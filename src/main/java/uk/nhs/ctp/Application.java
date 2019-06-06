package uk.nhs.ctp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CareConnectCareTeam;
import org.hl7.fhir.dstu3.model.CareConnectCondition;
import org.hl7.fhir.dstu3.model.CareConnectEncounter;
import org.hl7.fhir.dstu3.model.CareConnectEpisodeOfCare;
import org.hl7.fhir.dstu3.model.CareConnectHealthcareService;
import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.hl7.fhir.dstu3.model.CareConnectMedication;
import org.hl7.fhir.dstu3.model.CareConnectMedicationRequest;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.CareConnectProcedure;
import org.hl7.fhir.dstu3.model.CareConnectProcedureRequest;
import org.hl7.fhir.dstu3.model.CareConnectRelatedPerson;
import org.hl7.fhir.dstu3.model.CareConnectSpecimen;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;

import com.mifmif.common.regex.Generex;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@ServletComponentScan
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	public static void main(String[] args) {
		File file = new File("src/main/resources/templates/");
		
		if (!file.exists()) {
			file.mkdirs();
			file.setReadable(true, false);
		}
		
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
		processor.setValidator(validator());

		return processor;
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Bean
	public IParser fhirParser() {
		IParser fhirParser = fhirContext().newJsonParser();
		fhirParser.setPreferTypes(Arrays.asList(
			CareConnectCarePlan.class,
			CareConnectCareTeam.class,
			CareConnectCondition.class,
			CareConnectEncounter.class,
			CareConnectEpisodeOfCare.class,
			CareConnectHealthcareService.class,
			CareConnectLocation.class,
			CareConnectMedication.class,
			CareConnectMedicationRequest.class,
			CareConnectObservation.class,
			CareConnectOrganization.class,
			CareConnectPatient.class, 
			CareConnectPractitioner.class, 
			CareConnectProcedure.class,
			CareConnectProcedureRequest.class,
			CareConnectRelatedPerson.class,
			CareConnectSpecimen.class
		));
		
		return fhirParser;
	}
	
	@Bean
	public FhirContext fhirContext() {
		FhirContext fhirContext = FhirContext.forDstu3();
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-CarePlan-1", 
				CareConnectCarePlan.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-CareTeam-1", 
				CareConnectCareTeam.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Condition-1", 
				CareConnectCondition.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Encounter-1", 
				CareConnectEncounter.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-EpisodeOfCare-1", 
				CareConnectEpisodeOfCare.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-HealthcareService-1", 
				CareConnectHealthcareService.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Location-1", 
				CareConnectLocation.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Medication-1", 
				CareConnectMedication.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-MedicationRequest-1", 
				CareConnectMedicationRequest.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Observation-1", 
				CareConnectObservation.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Organization-1", 
				CareConnectOrganization.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Patient-1", 
				CareConnectPatient.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Practitioner-1", 
				CareConnectPractitioner.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Procedure-1", 
				CareConnectProcedure.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-ProcedureRequest-1", 
				CareConnectProcedureRequest.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-RelatedPerson-1", 
				CareConnectRelatedPerson.class);
		
		fhirContext.setDefaultTypeForProfile(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Specimen-1", 
				CareConnectSpecimen.class);
		
		return fhirContext;
	}
	
	@Bean
	public Generex uuidGenerator() {
		return new Generex("[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}");
	}
	
	@Bean
	public SimpleDateFormat reportDateFormat() {
		return new SimpleDateFormat("yyyyMMddHHmmss");
	}

}
