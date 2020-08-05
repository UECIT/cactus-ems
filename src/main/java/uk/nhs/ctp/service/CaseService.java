package uk.nhs.ctp.service;

import static uk.nhs.ctp.SystemConstants.DATE_FORMAT;

import com.google.common.base.Preconditions;
import java.time.Clock;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemURL;
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

  @Transactional
  public List<CaseParameter> getCaseParameters(Long id) {
    return findCase(id).getParameters();
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

    if (patient.hasName()) {
      HumanName name = patient.getNameFirstRep();
      if (name.hasGiven()) {
        triageCase.setFirstName(name.getGivenAsSingleString());
      }
      triageCase.setLastName(name.getFamily());
    }

    triageCase.setCreatedDate(Date.from(clock.instant()));

    if (patient.hasIdentifier()) {
      //TODO: CDSCT-482 - find the NHS number properly
      triageCase.setNhsNumber(patient.getIdentifierFirstRep().getValue());
    }

    // Initial patient observations
    Reference subject = referenceService
        .buildRef(ResourceType.Patient, triageCase.getPatientId());
    Reference context = referenceService.buildRef(ResourceType.Encounter, triageCase.getId());

    if (patient.hasGender()) {
      Observation genderObs = genderObservation(subject, context, patient.getGender());
      CaseParameter genderObsParameter = new CaseParameter();
      genderObsParameter.setTimestamp(genderObs.getIssued());
      genderObsParameter.setReference(storageService.storeExternal(genderObs));
      triageCase.addParameter(genderObsParameter);
    }

    if (patient.hasBirthDate()) {
      Observation ageObs = dateOfBirthObservation(subject, context, patient.getBirthDate());
      CaseParameter ageObsParameter = new CaseParameter();
      ageObsParameter.setTimestamp(ageObs.getIssued());
      ageObsParameter.setReference(storageService.storeExternal(ageObs));
      triageCase.addParameter(ageObsParameter);
    }

    caseRepository.saveAndFlush(triageCase);
  }

  private Observation dateOfBirthObservation(Reference subject, Reference context, Date date) {
    String dateOfBirth = DATE_FORMAT.format(date);
    Observation ageObs = new Observation()
        .setStatus(ObservationStatus.FINAL)
        .setIssued(Date.from(clock.instant()))
        .setCode(new CodeableConcept(new Coding(SystemURL.SNOMED, "397669002", "Age")))
        .setValue(new StringType(dateOfBirth))
        .setSubject(subject)
        .setContext(context);
    ageObs.setText(narrativeService.buildNarrative("Observed that 'Age' was " + dateOfBirth));
    return ageObs;
  }

  private Observation genderObservation(Reference subject, Reference context, AdministrativeGender gender) {
    Gender genderEnum = Gender.fromCode(gender.toCode());
    Observation genderObs = new Observation()
        .setStatus(ObservationStatus.FINAL)
        .setIssued(Date.from(clock.instant()))
        .setCode(new CodeableConcept(new Coding(SystemURL.SNOMED, "263495000", "Gender")))
        .setValue(genderEnum.toCodeableConcept())
        .setSubject(subject)
        .setContext(context);
    genderObs.setText(narrativeService.buildNarrative("Observed that 'Gender' was " + genderEnum.getDisplay()));
    return genderObs;
  }

  @Transactional
  public void updateCase(Long caseId, CdssResult evaluateResponse) {
    Cases triageCase = findCase(caseId);

    // 'Soft' delete existing parameters and create new ones
    triageCase.getParameters()
        .forEach(param -> param.setDeleted(true));

    if (evaluateResponse.hasOutputData()) {
      for (ParametersParameterComponent parameter : evaluateResponse.getOutputData().getParameter()) {
        addCaseParameter(parameter, triageCase);
      }
    }

    if (evaluateResponse.getResult() != null && evaluateResponse.getSwitchTrigger() == null) {
      triageCase.setTriageComplete(true);
      triageCase.setClosedDate(Date.from(clock.instant()));
    }

    caseRepository.saveAndFlush(triageCase);
  }

  private void addCaseParameter(ParametersParameterComponent parameter, Cases triageCase) {
    CaseParameter caseParameter = new CaseParameter();
    caseParameter.setTimestamp(Date.from(clock.instant()));

    String paramReference;
    if (parameter.hasValue() && parameter.getValue().hasType(FHIRAllTypes.REFERENCE.toCode())) {
      paramReference = ((Reference)parameter.getValue()).getReference();
    }
    else if (parameter.hasResource()) {
      paramReference = storageService.storeExternal(parameter.getResource());
    }
    else {
      log.warn("Output Parameter with name {} was not a resource or reference", parameter.getName());
      return;
    }
    caseParameter.setReference(paramReference);
    triageCase.addParameter(caseParameter);
  }

  public void addResourceToCaseInputData(Long caseId, Resource resource) {
    Cases existingCase = findCase(caseId);

    CaseParameter caseParameter = new CaseParameter();
    caseParameter.setReference(storageService.storeExternal(resource));
    caseParameter.setTimestamp(Date.from(clock.instant()));
    existingCase.addParameter(caseParameter);
    caseRepository.saveAndFlush(existingCase);
  }

}
