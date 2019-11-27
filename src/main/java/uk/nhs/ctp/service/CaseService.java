package uk.nhs.ctp.service;

import java.util.Date;
import java.util.List;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.exceptions.FHIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.entities.TestScenario;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.repos.TestScenarioRepository;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
public class CaseService {

  private static final Logger LOG = LoggerFactory.getLogger(CaseService.class);

  @Autowired
  private CaseRepository caseRepository;

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private TestScenarioRepository testScenarioRepository;

  /**
   * Create new case from patient ID and store in database
   *
   * @param patientId {@link Long}
   * @return {@link Cases}
   */
  public Cases createCase(Long patientId) {

    PatientEntity patient = ErrorHandlingUtils.checkEntityExists(
        patientRepository.findById(patientId), "Patient");

    TestScenario testScenario = testScenarioRepository.findByPatientId(patientId);
    ErrorHandlingUtils.checkEntityExists(testScenario, "Test Scenario");

    Cases triageCase = new Cases();

    LOG.info("Creating case for patient: " + patient.getFirstName());

    setCaseDetails(patient, testScenario, triageCase);

    return caseRepository.save(triageCase);
  }

  private void setCaseDetails(PatientEntity patient, TestScenario testScenario, Cases triageCase) {
    triageCase.setFirstName(patient.getFirstName());
    triageCase.setLastName(patient.getLastName());
    triageCase.setAddress(patient.getAddress());
    triageCase.setNhsNumber(patient.getNhsNumber());
    triageCase.setGender(patient.getGender());
    triageCase.setDateOfBirth(patient.getDateOfBirth());
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
    Cases triageCase = ErrorHandlingUtils.checkEntityExists(
        caseRepository.findById(caseId), "Case");

    LOG.info("Updating case for " + triageCase.getId());

    triageCase.setSessionId(sessionId);

    outputDataResources.forEach(resource -> {
      if (resource instanceof Observation) {
        boolean amended = false;
        Observation currentObs = (Observation) resource;
        if (currentObs.getValue().hasType("boolean")) {
          for (CaseObservation observation : triageCase.getObservations()) {
            if (observation.getCode()
                .equalsIgnoreCase(currentObs.getCode().getCoding().get(0).getCode())) {
              LOG.info("Amending Observation for case " + triageCase.getId());
              updateObservationCoding(currentObs, observation);
              observation.setTimestamp(new Date());
              amended = true;
            }
          }

          if (!amended) {
            LOG.info("Adding Observation for case " + triageCase.getId());
            triageCase.addObservation(createCaseObservation((Observation) resource));
          }
        }
      } else if (resource instanceof Immunization) {
        boolean amended = false;
        Immunization currentImm = (Immunization) resource;
        for (CaseImmunization immunisation : triageCase.getImmunizations()) {
          if (immunisation.getCode()
              .equalsIgnoreCase(currentImm.getVaccineCode().getCodingFirstRep().getCode())) {
            LOG.info("Amending Immunisation for case " + triageCase.getId());
            updateImmunisationCoding(currentImm, immunisation);
            immunisation.setTimestamp(new Date());
          }
        }

        if (!amended) {
          LOG.info("Adding Immunization for case " + triageCase.getId());
          triageCase.addImmunization(createCaseImmunization((Immunization) resource));
        }
      } else if (resource instanceof MedicationAdministration) {
        boolean amended = false;
        MedicationAdministration currentMed = (MedicationAdministration) resource;
        for (CaseMedication medicationAdmin : triageCase.getMedications()) {
          try {
            if (medicationAdmin.getCode().equalsIgnoreCase(
                currentMed.getMedicationCodeableConcept().getCodingFirstRep().getCode())) {
              LOG.info("Amending Medication for case " + triageCase.getId());
              updateMedicationCoding(currentMed, medicationAdmin);
              medicationAdmin.setTimestamp(new Date());
            }
          } catch (FHIRException e) {
            LOG.error(e.getMessage());
          }
        }

        if (!amended) {
          LOG.info("Adding Medication for case " + triageCase.getId());
          triageCase.addMedication(createCaseMedication((MedicationAdministration) resource));
        }
      }
      // add code here to deal with storing any items that do not match the above
      else {
        Parameters currentParameters = (Parameters) resource;
        ParametersParameterComponent currentParameter = currentParameters.getParameterFirstRep();

        triageCase.addParameter(createCaseParameter(currentParameter));
      }

    });

    return caseRepository.save(triageCase);
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
    caseObservation.setCode(coding.getCode());
    caseObservation.setDisplay(coding.getDisplay());
    try {
      caseObservation.setValue(observation.getValueBooleanType().booleanValue());
    } catch (FHIRException e) {
      LOG.error("Unable to get boolean type", e);
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
      LOG.error("Unable to fetch medication codeable concept", e);
    }
  }

}
