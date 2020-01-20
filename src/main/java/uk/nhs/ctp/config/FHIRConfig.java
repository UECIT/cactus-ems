package uk.nhs.ctp.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
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
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class FHIRConfig {


  @Value("${fhir.server}")
  private String fhirServer;

  @Value("${fhir.server.auth.token}")
  private String fhirServerAuthToken;

  @Bean()
  public IGenericClient fhirClient() {
    IGenericClient fhirClient = fhirContext().newRestfulGenericClient(fhirServer);
    fhirClient.registerInterceptor(new IClientInterceptor() {
      @Override
      public void interceptRequest(IHttpRequest theRequest) {
        if (theRequest.getUri().startsWith(fhirServer)) {
          theRequest.addHeader(HttpHeaders.AUTHORIZATION, fhirServerAuthToken);
        }
      }

      @Override
      public void interceptResponse(IHttpResponse theResponse) {
      }
    });
    return fhirClient;
  }

  @Bean
  public IParser fhirParser() {
    IParser fhirParser = fhirContext().newJsonParser();
    fhirParser.setServerBaseUrl(fhirServer);
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

    fhirContext.registerCustomType(CoordinateResource.class);

    return fhirContext;
  }
}
