package uk.nhs.ctp.service;

import static com.google.common.collect.MoreCollectors.onlyElement;
import static uk.nhs.ctp.SystemConstants.DATE_FORMAT;

import com.google.common.base.Preconditions;
import java.util.Date;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.QuestionResponse;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.PractitionerDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.transform.CaseObservationTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseService {

  private final CaseRepository caseRepository;
  private final GenericResourceLocator resourceLocator;
  private final StorageService storageService;
  private final CaseObservationTransformer caseObservationTransformer;
  private final TokenAuthenticationService authenticationService;

  public Cases createCase(String patientRef, PractitionerDTO practitioner) {
    String resourceType = new Reference(patientRef).getReferenceElement().getResourceType();
    Preconditions.checkArgument(resourceType.equalsIgnoreCase("Patient"),
        "Case must be created with a Patient resource");

    Patient patientResource = resourceLocator.findResource(patientRef);
    return createCase(patientResource, practitioner);
  }

  /**
   * Create new case from patient resource
   */
  public Cases createCase(Patient patient, PractitionerDTO practitioner) {

    log.info("Creating case for patient: " + patient.getNameFirstRep().getNameAsSingleString());

    Cases triageCase = new Cases();
    triageCase.setPatientId(patient.getId());
    triageCase.setSupplierId(authenticationService.requireSupplierId());

    if (practitioner != null) {
      triageCase.setPractitionerId(practitioner.getId());
    }
    setCaseDetails(triageCase, patient);

    // Store a mostly empty encounter record for future reference
    return caseRepository.saveAndFlush(triageCase);
  }

  private void setCaseDetails(Cases triageCase, Patient patient) {
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
    triageCase.setCreatedDate(new Date());

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
  @Transactional
  public Cases updateCase(Long caseId, CdssResult evaluateResponse, String sessionId) {
    Cases triageCase = caseRepository.findOne(caseId);
    ErrorHandlingUtils.checkEntityExists(triageCase, "Case");
    triageCase.setSessionId(sessionId);
    caseRepository.saveAndFlush(triageCase);

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

    if (evaluateResponse.getResult() != null && evaluateResponse.getSwitchTrigger() == null) {
      triageCase.setTriageComplete(true);
      triageCase.setClosedDate(new Date());
    }

    return caseRepository.saveAndFlush(triageCase);
  }

  private void updateQuestionnaireResponse(Cases triageCase, QuestionnaireResponse response) {
    //noinspection UnstableApiUsage
    QuestionResponse existingResponse = triageCase.getQuestionResponses().stream()
        .filter(answer -> answer.getQuestionnaireId()
            .equals(response.getQuestionnaire().getReference()))
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
          log.info("Amending Medication {} for case {}", medicationAdmin.getCode(),
              triageCase.getId());
          updateMedicationCoding(currentMed, medicationAdmin);

          amended = true;
        }
      } catch (FHIRException e) {
        log.error(e.getMessage());
      }
    }

    if (!amended) {
      log.info("Adding Medication {} for case {}",
          currentMed.getMedicationCodeableConcept().getCodingFirstRep(), triageCase.getId());
      triageCase.addMedication(createCaseMedication(currentMed));
    }
  }

  private void updateImmunization(Cases triageCase, Immunization resource) {
    boolean amended = false;
    for (CaseImmunization immunisation : triageCase.getImmunizations()) {
      if (immunisation.getCode()
          .equalsIgnoreCase(resource.getVaccineCode().getCodingFirstRep().getCode())) {
        log.info("Amending Immunisation {} for case {}", immunisation.getCode(),
            triageCase.getId());
        updateImmunisationCoding(resource, immunisation);

        amended = true;
      }
    }

    if (!amended) {
      log.info("Adding Immunization {} for case {}", resource.getVaccineCode().getCodingFirstRep(),
          triageCase.getId());
      triageCase.addImmunization(createCaseImmunization(resource));
    }
  }

  private void updateObservation(Cases triageCase, Observation currentObs) {
    boolean amended = false;
    for (CaseObservation observation : triageCase.getObservations()) {
      if (observation.getCode()
          .equalsIgnoreCase(currentObs.getCode().getCoding().get(0).getCode())) {
        log.info("Amending Observation {}-{} for case {}",
            observation.getDisplay(),
            ObjectUtils.defaultIfNull(observation.getValueDisplay(), observation.getValueCode()),
            triageCase.getId());

        caseObservationTransformer.updateObservationCoding(currentObs, observation);

        amended = true;
        break;
      }
    }

    if (!amended) {
      CaseObservation caseObs = caseObservationTransformer.transform(currentObs);
      log.info("Adding Observation {}-{} for case {}",
          caseObs.getDisplay(),
          caseObs.getValueDisplay(),
          triageCase.getId());
      triageCase.addObservation(caseObs);
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
    if (!existingCase.getObservations().contains(observation)) {
      existingCase.addObservation(observation);
      caseRepository.save(existingCase);
    }
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
}
