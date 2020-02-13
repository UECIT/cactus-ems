package uk.nhs.ctp.service;

import static com.google.common.collect.MoreCollectors.onlyElement;
import static uk.nhs.ctp.SystemConstants.DATE_FORMAT;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.CaseCarePlan;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.QuestionResponse;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.entities.TestScenario;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.repos.TestScenarioRepository;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.transform.CaseObservationTransformer;
import uk.nhs.ctp.transform.ReferralRequestTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@AllArgsConstructor
@Slf4j
public class CaseService {

  private CaseRepository caseRepository;
  private TestScenarioRepository testScenarioRepository;
  private GenericResourceLocator resourceLocator;
  private StorageService storageService;
  private CaseObservationTransformer caseObservationTransformer;
  private ReferralRequestService referralRequestService;
  private ReferralRequestTransformer referralRequestTransformer;
  private ReferenceService referenceService;

  /**
   * Create new case from patient ID
   *
   * @param patientId {@link Long}
   * @return {@link Cases}
   */
  public Cases createCase(String patientId, String practitionerId) {
    // TODO use test scenario provided by EMS UI
    TestScenario testScenario = testScenarioRepository.findByPatientId(1L);
    ErrorHandlingUtils.checkEntityExists(testScenario, "Test Scenario");
    return createCase(patientId, practitionerId, testScenario);
  }

  /**
   * Create new case from patient resource reference
   */
  public Cases createCase(String patientRef, String practitionerId, TestScenario testScenario) {
    String resourceType = new Reference(patientRef).getReferenceElement().getResourceType();
    Preconditions.checkArgument(resourceType.equalsIgnoreCase("Patient"),
        "Case must be created with a Patient resource");

    Patient patientResource = resourceLocator.findResource(patientRef);
    return createCase(patientResource, practitionerId, testScenario);
  }

  /**
   * Create new case from patient resource
   */
  public Cases createCase(Patient patient, String practitionerId, TestScenario testScenario) {

    log.info("Creating case for patient: " + patient.getNameFirstRep().getNameAsSingleString());

    Cases triageCase = new Cases();
    triageCase.setPatientId(patient.getId());
    triageCase.setPractitionerId(practitionerId);
    setCaseDetails(triageCase, patient, testScenario);

    // Store a mostly empty encounter record for future reference
    return caseRepository.saveAndFlush(triageCase);
  }

  private void setCaseDetails(Cases triageCase, Patient patient,
      TestScenario testScenario) {
    HumanName name = patient.getNameFirstRep();
    triageCase.setFirstName(name.getGivenAsSingleString());
    triageCase.setLastName(name.getFamily());

    // TODO handle address better
    if (patient.hasAddress()) {
      triageCase.setAddress(StringUtils.join(patient.getAddressFirstRep().getLine(), ", "));
    }

    if (patient.hasIdentifier()) {
      triageCase.setNhsNumber(patient.getIdentifierFirstRep().getId());
    }
    if (patient.hasGender()) {
      triageCase.setGender(patient.getGender().toCode());
    }
    triageCase.setDateOfBirth(patient.getBirthDate());

    triageCase.setSkillset(testScenario.getSkillset());
    triageCase.setParty(testScenario.getParty());
    triageCase.setTimestamp(new Date());

    // Patient observations
    CaseObservation genderObservation = new CaseObservation();
    genderObservation.setSystem(SystemURL.SNOMED);
    genderObservation.setCode("263495000");
    genderObservation.setDisplay("Gender");
    genderObservation.setValueSystem("string");
    genderObservation.setValueCode(triageCase.getGender());
    triageCase.addObservation(genderObservation);

    CaseObservation ageObservation = new CaseObservation();
    ageObservation.setSystem(SystemURL.SNOMED);
    ageObservation.setCode("397669002");
    ageObservation.setDisplay("Age");
    ageObservation.setValueSystem("string");
    ageObservation.setValueCode(DATE_FORMAT.format(triageCase.getDateOfBirth()));
    triageCase.addObservation(ageObservation);
  }

  /**
   * Convert Output Data Resources to Case Data Records and update Case
   *
   * @param caseId           {@link Long}
   * @param evaluateResponse results of a request to ServiceDefinition/[id]/$evaluate
   * @return {@link Cases}
   */
  public Cases updateCase(Long caseId, CdssResult evaluateResponse, String sessionId) {
    Cases triageCase = caseRepository.findOne(caseId);
    ErrorHandlingUtils.checkEntityExists(triageCase, "Case");

    log.info("Updating case for " + triageCase.getId());

    triageCase.setSessionId(sessionId);
    triageCase.setReferralRequest(null);
    caseRepository.saveAndFlush(triageCase);

    // Store referral request
    ReferralRequest referralRequest = evaluateResponse.getReferralRequest();
    if (referralRequest != null) {
      log.info("Storing referral request");
      ReferralRequest absoluteReferralRequest = referralRequestService
          .makeAbsolute(referralRequest);
      ReferralRequestEntity referralRequestEntity = referralRequestTransformer
          .transform(absoluteReferralRequest);
      triageCase.setReferralRequest(referralRequestEntity);
    }

    // Store references to CarePlans
    if (evaluateResponse.hasCareAdvice()) {
      Date now = new Date();
      List<CaseCarePlan> carePlans = triageCase.getCarePlans();
      carePlans.clear();

      evaluateResponse.getCareAdvice().stream()
          .map(dto -> CaseCarePlan.builder()
              .reference(dto.getId())
              .timestamp(now)
              .build())
          .forEach(carePlans::add);
    }

    // Store output data
    for (ParametersParameterComponent parameter : evaluateResponse.getOutputData().getParameter()) {
      var resource = parameter.getResource();
      if (SystemConstants.OUTPUT_DATA.equals(parameter.getName())) {
        if (resource instanceof Observation) {
          updateObservation(triageCase, (Observation) resource);
        } else if (resource instanceof Immunization) {
          updateImmunization(triageCase, (Immunization) resource);
        } else if (resource instanceof MedicationAdministration) {
          updateMedication(triageCase, (MedicationAdministration) resource);
        } else if (resource instanceof QuestionnaireResponse) {
          updateQuestionnaireResponse(triageCase, (QuestionnaireResponse) resource);
        }
      } else {
        triageCase.addParameter(createCaseParameter(parameter));
      }
    }

    return caseRepository.saveAndFlush(triageCase);
  }

  private void updateQuestionnaireResponse(Cases triageCase, QuestionnaireResponse response) {
    //noinspection UnstableApiUsage
    QuestionResponse existingResponse = triageCase.getQuestionResponses().stream()
        .filter(answer -> answer.getQuestionnaireId()
            .equals(response.getQuestionnaire().getReference().split("/")[1]))
        .collect(onlyElement());

    response.setId(existingResponse.getReference());

    try {
      storageService.updateExternal(response);
    } catch (Exception e) {
      log.error("Could not update questionnaire response with id " + response.getId() + "\n" + e
          .getMessage());
    }
  }

  private void updateMedication(Cases triageCase, MedicationAdministration currentMed) {
    boolean amended = false;
    for (CaseMedication medicationAdmin : triageCase.getMedications()) {
      try {
        if (medicationAdmin.getCode().equalsIgnoreCase(
            currentMed.getMedicationCodeableConcept().getCodingFirstRep().getCode())) {
          log.info("Amending Medication for case " + triageCase.getId());
          updateMedicationCoding(currentMed, medicationAdmin);
          medicationAdmin.setTimestamp(new Date());

          amended = true;
        }
      } catch (FHIRException e) {
        log.error(e.getMessage());
      }
    }

    if (!amended) {
      log.info("Adding Medication for case " + triageCase.getId());
      triageCase.addMedication(createCaseMedication(currentMed));
    }
  }

  private void updateImmunization(Cases triageCase, Immunization resource) {
    boolean amended = false;
    for (CaseImmunization immunisation : triageCase.getImmunizations()) {
      if (immunisation.getCode()
          .equalsIgnoreCase(resource.getVaccineCode().getCodingFirstRep().getCode())) {
        log.info("Amending Immunisation for case " + triageCase.getId());
        updateImmunisationCoding(resource, immunisation);
        immunisation.setTimestamp(new Date());

        amended = true;
      }
    }

    if (!amended) {
      log.info("Adding Immunization for case " + triageCase.getId());
      triageCase.addImmunization(createCaseImmunization(resource));
    }
  }

  private void updateObservation(Cases triageCase, Observation currentObs) {
    boolean amended = false;
    for (CaseObservation observation : triageCase.getObservations()) {
      if (observation.getCode()
          .equalsIgnoreCase(currentObs.getCode().getCoding().get(0).getCode())) {
        log.info("Amending Observation for case " + triageCase.getId());

        caseObservationTransformer.updateObservationCoding(currentObs, observation);
        observation.setTimestamp(new Date());

        amended = true;
      }
    }

    if (!amended) {
      log.info("Adding Observation for case " + triageCase.getId());
      triageCase.addObservation(caseObservationTransformer.transform(currentObs));
    }
  }

  /**
   * Create CaseImmunization from Immunization resource
   *
   * @param immunization {@link Immunization}
   * @return {@link CaseImmunization}
   */
  protected CaseImmunization createCaseImmunization(Immunization immunization) {
    CaseImmunization caseImmunization = new CaseImmunization();

    updateImmunisationCoding(immunization, caseImmunization);

    caseImmunization.setNotGiven(immunization.getNotGiven());
    caseImmunization.setTimestamp(new Date());

    return caseImmunization;
  }

  /**
   * Update the coding for a caseImmunisation
   *
   * @param immunization
   * @param caseImmunization
   */
  private void updateImmunisationCoding(Immunization immunization,
      CaseImmunization caseImmunization) {
    Coding coding = immunization.getVaccineCode().getCodingFirstRep();
    caseImmunization.setCode(coding.getCode());
    caseImmunization.setDisplay(coding.getDisplay());
  }

  public void addObservation(Long caseId, CaseObservation observation) {
    Cases existingCase = caseRepository.findOne(caseId);
    existingCase.addObservation(observation);
    caseRepository.save(existingCase);
  }

  /**
   * Create CaseParameter from ParametersParameterComponent resource
   *
   * @param parameter {@link ParametersParameterComponent}
   * @return {@link CaseParameter}
   */
  protected CaseParameter createCaseParameter(ParametersParameterComponent parameter) {
    CaseParameter caseParameter = new CaseParameter();
    // Try to set dataAbsenseReason here
    caseParameter.setName(parameter.getName());
    try {
      caseParameter.setValue(parameter.getValue().toString());
    } catch (Exception e) {

    }
    caseParameter.setTimestamp(new Date());

    return caseParameter;
  }

  /**
   * Create CaseMedication from MedicationAdministration resource
   *
   * @param medication {@link MedicationAdministration}
   * @return {@link CaseMedication}
   */
  protected CaseMedication createCaseMedication(MedicationAdministration medication) {
    CaseMedication caseMedication = new CaseMedication();

    updateMedicationCoding(medication, caseMedication);

    caseMedication.setNotGiven(medication.getNotGiven());
    caseMedication.setTimestamp(new Date());

    return caseMedication;
  }

  /**
   * Update the coding for a Medication
   *
   * @param medication
   * @param caseMedication
   */
  private void updateMedicationCoding(
      MedicationAdministration medication,
      CaseMedication caseMedication) {
    Coding coding;
    try {
      coding = medication.getMedicationCodeableConcept().getCodingFirstRep();
      caseMedication.setCode(coding.getCode());
      caseMedication.setDisplay(coding.getDisplay());
    } catch (FHIRException e) {
      log.error("Unable to fetch medication codeable concept", e);
    }
  }

  public Cases updateSelectedService(Long caseId, String selectedServiceId) {
    Cases triageCase = caseRepository.findOne(caseId);
    ErrorHandlingUtils.checkEntityExists(triageCase, "Case");

    log.info("Setting selected HealthcareService for case " + triageCase.getId());

    Reference serviceRef = new Reference(selectedServiceId);
    Preconditions.checkArgument(
        "HealthcareService".equals(serviceRef.getReferenceElement().getResourceType()),
        "Selected service must be a HealthcareService"
    );
    Preconditions.checkNotNull(triageCase.getReferralRequest(), "No referral request found");

    ReferralRequestEntity referralRequestEntity = triageCase.getReferralRequest();
    referralRequestService.update(referralRequestEntity, referralRequest -> {
      referralRequest.setRecipient(Collections.singletonList(serviceRef));
      referralRequest.addSupportingInfo(
          referenceService.buildRef(ResourceType.Appointment, "example-appointment"));
    });

    caseRepository.saveAndFlush(triageCase);
    return triageCase;
  }
}
