package uk.nhs.ctp.service;

import static uk.nhs.ctp.SystemConstants.DATE_FORMAT;

import com.google.common.base.Preconditions;
import java.time.Clock;
import java.util.Date;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.enums.Gender;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.PractitionerDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseService {

  private final CaseRepository caseRepository;
  private final GenericResourceLocator resourceLocator;
  private final StorageService storageService;
  private final TokenAuthenticationService authService;
  private final NarrativeService narrativeService;
  private final ReferenceService referenceService;
  private final Clock clock;

  public Cases findCase(Long id) {
    return caseRepository.getOneByIdAndSupplierId(id, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);
  }

  public Cases createCase(String patientRef, PractitionerDTO practitioner) {
    String resourceType = new Reference(patientRef).getReferenceElement().getResourceType();
    Preconditions.checkArgument(resourceType.equalsIgnoreCase("Patient"),
        "Case must be created with a Patient resource");

    log.info("Creating case for patient at: {}", patientRef);

    Cases triageCase = new Cases();
    triageCase.setPatientId(patientRef);
    triageCase.setSupplierId(authService.requireSupplierId());

    if (practitioner != null) {
      triageCase.setPractitionerId(practitioner.getId());
    }

    return caseRepository.saveAndFlush(triageCase);
  }

  public void setupCaseDetails(Cases triageCase, String patientRef) {
    Patient patient = resourceLocator.findResource(patientRef);
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

    // Initial patient observations
    Gender gender = Gender.fromCode(triageCase.getGender());
    Reference subject = referenceService
        .buildRef(ResourceType.Patient, triageCase.getPatientId());
    Reference context = referenceService.buildRef(ResourceType.Encounter, triageCase.getId());

    Observation genderObs = new Observation()
        .setStatus(ObservationStatus.FINAL)
        .setIssued(Date.from(clock.instant()))
        .setCode(new CodeableConcept(new Coding(SystemURL.SNOMED, "263495000", "Gender")))
        .setValue(gender.toCodeableConcept())
        .setSubject(subject)
        .setContext(context);
    genderObs.setText(narrativeService.buildNarrative("Observed that 'Gender' was " + gender.getDisplay()));
    CaseParameter genderObsParameter = new CaseParameter();
    genderObsParameter.setTimestamp(genderObs.getIssued());
    genderObsParameter.setReference(storageService.storeExternal(genderObs));
    triageCase.addParameter(genderObsParameter);

    Observation ageObs = new Observation()
        .setStatus(ObservationStatus.FINAL)
        .setIssued(Date.from(clock.instant()))
        .setCode(new CodeableConcept(new Coding(SystemURL.SNOMED, "397669002", "Age")))
        .setValue(new StringType(DATE_FORMAT.format(triageCase.getDateOfBirth())))
        .setSubject(subject)
        .setContext(context);
    ageObs.setText(narrativeService.buildNarrative("Observed that 'Gender' was " + gender.getDisplay()));
    CaseParameter ageObsParameter = new CaseParameter();
    ageObsParameter.setTimestamp(ageObs.getIssued());
    ageObsParameter.setReference(storageService.storeExternal(ageObs));
    triageCase.addParameter(ageObsParameter);
    caseRepository.saveAndFlush(triageCase);
  }

  @Transactional
  public Cases updateCase(Long caseId, CdssResult evaluateResponse) {
    Cases triageCase = caseRepository
        .getOneByIdAndSupplierId(caseId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);

    caseRepository.saveAndFlush(triageCase);

    // Delete existing parameters and create new ones
    triageCase.getParameters()
        .forEach(param -> param.setDeleted(true));

    for (ParametersParameterComponent parameter : evaluateResponse.getOutputData().getParameter()) {
      CaseParameter caseParameter = new CaseParameter();
      caseParameter.setTimestamp(Date.from(clock.instant()));

      String paramReference;
      if (parameter.hasValue()
          && parameter.getValue().hasType(FHIRAllTypes.REFERENCE.toCode())) {
        // Store the reference
        paramReference = ((Reference)parameter.getValue()).getReference();
      }
      else if (parameter.hasResource()) {
        // Save resource then store reference
        paramReference = storageService.storeExternal(parameter.getResource());
      }
      else {
        log.warn("Output Parameter with name {} was not a resource or reference", parameter.getName());
        continue;
      }

      caseParameter.setReference(paramReference);
      triageCase.addParameter(caseParameter);
    }

    if (evaluateResponse.getResult() != null && evaluateResponse.getSwitchTrigger() == null) {
      triageCase.setTriageComplete(true);
      triageCase.setClosedDate(new Date());
    }

    return caseRepository.saveAndFlush(triageCase);
  }

  public void addObservation(Long caseId, CaseObservation observation) {
    Cases existingCase = caseRepository
        .getOneByIdAndSupplierId(caseId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);
    if (!existingCase.getObservations().contains(observation)) {
      existingCase.addObservation(observation);
      caseRepository.save(existingCase);
    }
  }

}
