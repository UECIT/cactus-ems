package uk.nhs.ctp.service;

import static uk.nhs.ctp.SystemConstants.DATE_FORMAT;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationStatus;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.EncounterEntity;
import uk.nhs.ctp.enums.Language;
import uk.nhs.ctp.repos.EncounterRepository;
import uk.nhs.ctp.service.builder.ReferenceBuilder;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.PersonDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.service.factory.ReferenceBuilderFactory;
import uk.nhs.ctp.transform.ObservationTransformer;
import uk.nhs.ctp.transform.PersonTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@AllArgsConstructor
@Slf4j
public class ParametersService {

  private EncounterRepository encounterRepository;
  private ReferenceBuilderFactory referenceBuilderFactory;
  private AuditService auditService;
  private ReferenceService referenceService;
  private ObservationTransformer observationTransformer;
  private QuestionnaireService questionnaireService;
  private PersonTransformer personTransformer;

  Parameters getEvaluateParameters(
      Long caseId,
      TriageQuestion[] questionResponse,
      SettingsDTO settings,
      Boolean amending,
      ReferencingContext referencingContext,
      String questionnaireId
  ) {

    ReferenceBuilder referenceBuilder = referenceBuilderFactory.load(referencingContext);
    EncounterEntity caseEntity = encounterRepository.findFirstByIdVersion_IdOrderByIdVersion_VersionDesc(caseId);

    ErrorHandlingUtils.checkEntityExists(caseEntity, "Case");
    var caseAudit = auditService.getAuditRecordByCase(caseId);
    if (caseAudit == null) {
      throw new NullPointerException("Could not find an audit record for case " + caseId);
    }

    Builder builder = new Builder()
        .setRequestId(UUID.randomUUID().toString())
        .setEncounter(caseId)
        .setPatient(caseEntity.getPatientId())
        .setContext(caseEntity)

        // Add user context information
        .setUserType(settings.getUserType())
        .setUserLanguage(settings.getUserLanguage())
        .setUserTaskContext(settings.getUserTaskContext())
        .setInitiatingPerson(settings.getInitiatingPerson())
        .setReceivingPerson(settings.getReceivingPerson())
        .setRecipientType(settings.getRecipientType())
        .setRecipientLanguage(settings.getUserLanguage())
        .setSetting(settings.getSetting());

    builder.addQuestionnaireResponses(
        questionnaireService.updateEncounterResponses(
            caseEntity, questionnaireId, questionResponse, amending, referenceBuilder));

    getObservations(caseEntity).forEach(builder::addInputData);
    getImmunizations(caseEntity).forEach(builder::addInputData);
    getMedications(caseEntity).forEach(builder::addInputData);

    // Add extra parameters
    // TODO review this in more detail - not sent by CDS?
    addParameterInputData(caseEntity, builder);

    return builder.build();
  }

  private class Builder {

    private final HashMultimap<String, ParametersParameterComponent> parameterNames;
    private final Parameters parameters;

    public Builder() {
      parameters = new Parameters();
      parameters.setMeta(new Meta().addProfile(SystemURL.SERVICE_DEFINITION_EVALUATE));
      parameterNames = HashMultimap.create();
    }

    public Parameters build() {
      return parameters;
    }

    private ParametersParameterComponent addParameter(String name) {
      ParametersParameterComponent param = parameters.addParameter().setName(name);
      parameterNames.put(name, param);
      return param;
    }

    private ParametersParameterComponent addUniqueParameter(String name) {
      Preconditions.checkArgument(!parameterNames.containsKey(name),
          "Parameter " + name + " must be unique");
      return addParameter(name);
    }

    private CodeableConcept toSnomedCode(CodeDTO typeCodeDTO) {
      return new CodeableConcept()
          .setText(typeCodeDTO.getDisplay())
          .addCoding(new Coding()
              .setCode(typeCodeDTO.getCode())
              .setDisplay(typeCodeDTO.getDisplay())
              .setSystem(SystemURL.SNOMED));
    }

    public Builder setRequestId(String requestId) {
      addUniqueParameter(SystemConstants.REQUEST_ID)
          .setValue(new IdType(requestId));
      return this;
    }

    public Builder setContext(EncounterEntity caseEntity) {
      Parameters inputParamsResource = new Parameters();

      inputParamsResource.addParameter().setName(SystemConstants.CONTEXT)
          .addPart(new ParametersParameterComponent().setName(SystemConstants.PARTY)
              .setValue(new StringType(caseEntity.getParty().getCode())))
          .addPart(new ParametersParameterComponent().setName(SystemConstants.SKILLSET)
              .setValue(new StringType(caseEntity.getSkillset().getCode())));

      addUniqueParameter(SystemConstants.INPUT_PARAMETERS)
          .setResource(inputParamsResource);
      return this;
    }

    public Builder setEncounter(Long caseId) {
      addUniqueParameter(SystemConstants.ENCOUNTER)
          .setValue(referenceService.buildRef(ResourceType.Encounter, caseId));

      return this;
    }

    public Builder setPatient(String patientId) {
      addUniqueParameter(SystemConstants.PATIENT)
          .setValue(new Reference(patientId));
      return this;
    }

    public Builder addQuestionnaireResponses(List<QuestionnaireResponse> questionnaireResponses) {
      for (QuestionnaireResponse resource : questionnaireResponses) {
        addParameter(SystemConstants.INPUT_DATA)
            .setResource(resource);
      }
      return this;
    }

    public Builder setInitiatingPerson(PersonDTO personDto) {
      Person person = personTransformer.transform(personDto);

      addUniqueParameter(SystemConstants.INITIATINGPERSON)
          .setResource(person);

      return this;
    }

    public Builder setReceivingPerson(PersonDTO personDTO) {
      Person person = personTransformer.transform(personDTO);

      addUniqueParameter(SystemConstants.RECEIVINGPERSON)
          .setResource(person);

      return this;
    }

    public Builder setUserType(CodeDTO typeCodeDTO) {
      CodeableConcept typeCode = toSnomedCode(typeCodeDTO);

      addUniqueParameter(SystemConstants.USERTYPE)
          .setValue(typeCode);

      return this;
    }

    public Builder setRecipientType(CodeDTO typeCodeDTO) {
      CodeableConcept typeCode = toSnomedCode(typeCodeDTO);

      addUniqueParameter(SystemConstants.RECIPIENTTYPE)
          .setValue(typeCode);

      return this;
    }

    public Builder setUserLanguage(CodeDTO languageDTO) {
      CodeableConcept language =
          Language.fromCode(languageDTO.getCode()).toCodeableConcept();

      addUniqueParameter(SystemConstants.USERLANGUAGE)
          .setValue(language);

      return this;
    }

    public Builder setRecipientLanguage(CodeDTO languageDTO) {
      CodeableConcept language =
          Language.fromCode(languageDTO.getCode()).toCodeableConcept();

      addUniqueParameter(SystemConstants.RECIPIENTLANGUAGE)
          .setValue(language);

      return this;
    }

    public Builder setUserTaskContext(CodeDTO contextDTO) {
      CodeableConcept context = toSnomedCode(contextDTO);

      addUniqueParameter(SystemConstants.USERTASKCONTEXT)
          .setValue(context);

      return this;
    }

    public Builder setSetting(CodeDTO settingDTO) {
      CodeableConcept setting = toSnomedCode(settingDTO);

      addUniqueParameter(SystemConstants.SETTING)
          .setValue(setting);

      return this;
    }

    public Builder addInputData(Resource resource) {
      addParameter(SystemConstants.INPUT_DATA)
          .setResource(resource);

      return this;
    }
  }

  private ArrayList<Immunization> getImmunizations(EncounterEntity caseEntity) {
    ArrayList<Immunization> immunizations = new ArrayList<>();

    if (!caseEntity.getImmunizations().isEmpty()) {
      for (CaseImmunization immunizationEntity : caseEntity.getImmunizations()) {
        Immunization immunization = new Immunization()
            .setStatus(Immunization.ImmunizationStatus.COMPLETED)
            .setVaccineCode(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED,
                immunizationEntity.getCode(), immunizationEntity.getDisplay())))
            .setNotGiven(immunizationEntity.getNotGiven());

        immunizations.add(immunization);
      }
    }

    return immunizations;
  }

  private ArrayList<MedicationAdministration> getMedications(EncounterEntity caseEntity) {
    ArrayList<MedicationAdministration> medications = new ArrayList<>();
    if (!caseEntity.getMedications().isEmpty()) {
      for (CaseMedication medicationEntity : caseEntity.getMedications()) {
        MedicationAdministration medication = new MedicationAdministration()
            .setStatus(MedicationAdministrationStatus.COMPLETED)
            .setMedication(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED,
                medicationEntity.getCode(), medicationEntity.getDisplay())))
            .setNotGiven(medicationEntity.getNotGiven());

        medications.add(medication);
      }
    }

    return medications;
  }

  private Collection<Observation> getObservations(EncounterEntity caseEntity) {

    var observations = new TreeMap<CodeableConcept, Observation>(
        Comparator.comparing((CodeableConcept a) -> a.getCodingFirstRep().getCode())
            .thenComparing(a -> a.getCodingFirstRep().getSystem())
    );

    // Patient observations
    Observation genderObservation = new CareConnectObservation()
        .setStatus(Observation.ObservationStatus.FINAL)
        .setCode(
            new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED, "263495000", "Gender")))
        .setIssued(caseEntity.getTimestamp())
        .setValue(new StringType(caseEntity.getGender()));
    observations.put(genderObservation.getCode(), genderObservation);

    Observation ageObservation = new CareConnectObservation()
        .setStatus(Observation.ObservationStatus.FINAL)
        .setCode(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED, "397669002", "Age")))
        .setIssued(caseEntity.getTimestamp())
        .setValue(new StringType(DATE_FORMAT.format(caseEntity.getDateOfBirth())));
    observations.put(ageObservation.getCode(), ageObservation);

    // CDSS Observations
    for (CaseObservation oe : caseEntity.getObservations()) {
      Observation observation = observationTransformer.transform(oe);
      observations.put(observation.getCode(), observation);
    }

    return observations.values();
  }

  private void addParameterInputData(EncounterEntity caseEntity, Builder builder) {
    if (!caseEntity.getParameters().isEmpty()) {
      for (CaseParameter parameterEntity : caseEntity.getParameters()) {
        builder.addParameter(parameterEntity.getName())
            .setValue(new StringType(parameterEntity.getValue()));
      }
    }
  }
}
