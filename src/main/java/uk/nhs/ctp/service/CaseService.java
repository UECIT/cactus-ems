package uk.nhs.ctp.service;

import static com.google.common.collect.MoreCollectors.onlyElement;

import com.google.common.base.Preconditions;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.entities.QuestionResponse;
import uk.nhs.ctp.entities.TestScenario;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.repos.TestScenarioRepository;
import uk.nhs.ctp.service.builder.CareConnectPatientBuilder;
import uk.nhs.ctp.service.encounter.EncounterService;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@AllArgsConstructor
@Slf4j
public class CaseService {

  private CaseRepository caseRepository;
  private PatientRepository patientRepository;
  private TestScenarioRepository testScenarioRepository;
  private StorageService storageService;
  private EncounterService encounterService;
  private CareConnectPatientBuilder careConnectPatientBuilder;

  /**
   * Create new case from patient ID
   *
   * @param patientId {@link Long}
   * @return {@link Cases}
   */
  public Cases createCase(Long patientId) {

    PatientEntity patient = patientRepository.findOne(patientId);
    ErrorHandlingUtils.checkEntityExists(patient, "Patient");

    // TODO use test scenario provided by EMS UI
    TestScenario testScenario = testScenarioRepository.findByPatientId(patientId);
    ErrorHandlingUtils.checkEntityExists(testScenario, "Test Scenario");

    CareConnectPatient patientResource = careConnectPatientBuilder.build(patient);
    return createCase(patientResource, testScenario);
  }

  /**
   * Create new case from patient resource reference
   */
  public Cases createCase(String patientRef, TestScenario testScenario) {
    String resourceType = new Reference(patientRef).getReferenceElement().getResourceType();
    Preconditions.checkArgument(resourceType.equalsIgnoreCase("Patient"),
        "Case must be created with a Patient resource");

    CareConnectPatient patientResource = storageService
        .findResource(patientRef, CareConnectPatient.class);

    return createCase(patientResource, testScenario);
  }

  /**
   * Create new case from patient resource
   */
  public Cases createCase(CareConnectPatient patient, TestScenario testScenario) {

    log.info("Creating case for patient: " + patient.getNameFirstRep().getNameAsSingleString());

    Cases triageCase = new Cases();
    triageCase.setPatientId(patient.getId());
    setCaseDetails(triageCase, patient, testScenario);

    // Store a mostly empty encounter record for future reference
    triageCase.setEncounterId(encounterService.createEncounter(triageCase));
    return caseRepository.saveAndFlush(triageCase);
  }

  private void setCaseDetails(Cases triageCase, CareConnectPatient patient,
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
  }

  /**
   * Convert Output Data Resources to Case Data Records and update Case
   *
   * @param caseId              {@link Long}
   * @param outputDataResources {@link List} of {@link Resource}
   * @return {@link Cases}
   */
  public Cases updateCase(Long caseId, List<Resource> outputDataResources, String sessionId) {
    Cases triageCase = caseRepository.findOne(caseId);
    ErrorHandlingUtils.checkEntityExists(triageCase, "Case");

    log.info("Updating case for " + triageCase.getId());

    triageCase.setSessionId(sessionId);

    outputDataResources.forEach(resource -> {
      if (resource instanceof Observation) {
        updateObservation(triageCase, (Observation) resource);
      } else if (resource instanceof Immunization) {
        updateImmunization(triageCase, (Immunization) resource);
      } else if (resource instanceof MedicationAdministration) {
        updateMedication(triageCase, (MedicationAdministration) resource);
      } else if (resource instanceof QuestionnaireResponse) {
        updateQuestionnaireResponse(triageCase, (QuestionnaireResponse) resource);
      } else if (resource instanceof Parameters) {
        Parameters currentParameters = (Parameters) resource;
        ParametersParameterComponent currentParameter = currentParameters.getParameterFirstRep();
        triageCase.addParameter(createCaseParameter(currentParameter));
      } else {
        // TODO add code here to deal with storing any items that do not match the above
        log.warn("Unsupported outputParameter type: {}" + resource.getResourceType().name());
      }

    });

    return caseRepository.save(triageCase);
  }

  private void updateQuestionnaireResponse(Cases triageCase, QuestionnaireResponse response) {
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
    Immunization currentImm = resource;
    for (CaseImmunization immunisation : triageCase.getImmunizations()) {
      if (immunisation.getCode()
          .equalsIgnoreCase(currentImm.getVaccineCode().getCodingFirstRep().getCode())) {
        log.info("Amending Immunisation for case " + triageCase.getId());
        updateImmunisationCoding(currentImm, immunisation);
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

        updateObservationCoding(currentObs, observation);
        observation.setTimestamp(new Date());

        amended = true;
      }
    }

    if (!amended) {
      log.info("Adding Observation for case " + triageCase.getId());
      triageCase.addObservation(createCaseObservation(currentObs));
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

  /**
   * Create CaseObservation from Observation resource
   *
   * @param observation {@link Observation}
   * @return {@link CaseObservation}
   */
  protected CaseObservation createCaseObservation(Observation observation) {
    CaseObservation caseObservation = new CaseObservation();

    updateObservationCoding(observation, caseObservation);

    // Try to set dataAbsenseReason here
    Coding dataAbsentReason = observation.getDataAbsentReason().getCodingFirstRep();
    caseObservation.setDataAbsentCode(dataAbsentReason.getCode());
    caseObservation.setDataAbsentDisplay(dataAbsentReason.getDisplay());
    caseObservation.setTimestamp(new Date());

    return caseObservation;
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
   * Update the coding a for a given observation
   *
   * @param observation
   * @param caseObservation
   */
  private void updateObservationCoding(Observation observation, CaseObservation caseObservation) {
    Coding coding = observation.getCode().getCodingFirstRep();
    caseObservation.setSystem(coding.getSystem());
    caseObservation.setCode(coding.getCode());
    caseObservation.setDisplay(coding.getDisplay());

    if (!observation.hasValue()) {
      caseObservation.setValueCode(null);
      caseObservation.setValueSystem(null);
      caseObservation.setValueDisplay(null);
    } else if (observation.getValue() instanceof BooleanType) {
      boolean value = observation.getValueBooleanType().booleanValue();
      caseObservation.setValueSystem("boolean");
      caseObservation.setValueCode(value ? "true" : "false");
    } else if (observation.getValue() instanceof StringType) {
      String value = observation.getValueStringType().getValue();
      caseObservation.setValueSystem("string");
      caseObservation.setValueCode(value);
    } else if (observation.getValue() instanceof CodeableConcept) {
      Coding valueCoding = observation.getValueCodeableConcept().getCodingFirstRep();
      caseObservation.setValueSystem(valueCoding.getSystem());
      caseObservation.setValueCode(valueCoding.getCode());
      caseObservation.setDisplay(valueCoding.getDisplay());
    } else {
      log.error("Unable assign an observation value of type {}", observation.getValue().fhirType());
    }

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
  private void updateMedicationCoding(MedicationAdministration medication,
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

}
