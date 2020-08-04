package uk.nhs.ctp.service.builder;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IIdType;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.enums.Language;
import uk.nhs.ctp.enums.Setting;
import uk.nhs.ctp.enums.UserType;
import uk.nhs.ctp.service.Validator;
import uk.nhs.ctp.service.dto.CodeDTO;

@Slf4j
public class EvaluateParametersBuilder {

  private static final Set<String> VALID_INITIATING_PERSON_TYPES =
      Set.of("Patient", "RelatedPerson", "Practitioner");
  private static final Set<String> VALID_RECEIVING_PERSON_TYPES =
      Set.of("Patient", "RelatedPerson");

  private final HashMultimap<String, ParametersParameterComponent> parameterNames;
  private final Parameters parameters;

  public EvaluateParametersBuilder() {
    parameters = new Parameters();
    parameters.setMeta(new Meta().addProfile(SystemURL.SERVICE_DEFINITION_EVALUATE));
    parameterNames = HashMultimap.create();
  }

  public Parameters build() {
    validate();
    return parameters;
  }

  public void validate() {
    validate(SystemConstants.REQUEST_ID)
        .checkSingle()
        .checkType(IIdType.class);

    for (ParametersParameterComponent parameter : parameterNames
        .get(SystemConstants.INPUT_DATA)) {
      if (parameter.getResource() != null) {
        log.warn("Found embedded resource in inputData of type {}",
            parameter.getResource().getResourceType());
      }
    }

    validate(SystemConstants.PATIENT)
        .checkSingle()
        .checkReferenceType(ResourceType.Patient);

    validate(SystemConstants.ENCOUNTER)
        .checkSingle()
        .checkReferenceType(ResourceType.Encounter);

    validate(SystemConstants.INITIATINGPERSON)
        .checkSingle()
        .checkReferenceType(ResourceType.Patient, ResourceType.RelatedPerson,
            ResourceType.Practitioner);

    validate(SystemConstants.USERTYPE)
        .checkSingle()
        .checkCodeableConcept(VALID_INITIATING_PERSON_TYPES);

    validate(SystemConstants.RECEIVINGPERSON)
        .checkSingle()
        .checkReferenceType(ResourceType.Patient, ResourceType.RelatedPerson);

    validate(SystemConstants.SETTING)
        .checkSingle();

    validate("settingContext")
        .checkAbsent();
  }

  private Validator<Set<ParametersParameterComponent>> validate(String parameter) {
    return new Validator<>(parameterNames.get(parameter), parameter);
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

  public EvaluateParametersBuilder setRequestId(String requestId) {
    addUniqueParameter(SystemConstants.REQUEST_ID)
        .setValue(new IdType(requestId));
    return this;
  }

  public EvaluateParametersBuilder setEncounter(Reference encounterRef) {
    addUniqueParameter(SystemConstants.ENCOUNTER)
        .setValue(encounterRef);

    return this;
  }

  public EvaluateParametersBuilder setPatient(Reference patient) {
    Preconditions.checkArgument(patient.getReferenceElement().getResourceType().equals("Patient"),
        "Reference must be of type Patient");

    addUniqueParameter(SystemConstants.PATIENT)
        .setValue(patient);
    return this;
  }

  public EvaluateParametersBuilder addQuestionnaireResponses(List<QuestionnaireResponse> questionnaireResponses) {
    for (QuestionnaireResponse resource : questionnaireResponses) {
      addParameter(SystemConstants.INPUT_DATA)
          .setResource(resource);
    }
    return this;
  }

  public EvaluateParametersBuilder setInitiatingPerson(Reference reference) {
    addUniqueParameter(SystemConstants.INITIATINGPERSON)
        .setValue(reference);
    return this;
  }

  public EvaluateParametersBuilder setReceivingPerson(Reference reference) {
    addUniqueParameter(SystemConstants.RECEIVINGPERSON)
        .setValue(reference);

    return this;
  }

  public EvaluateParametersBuilder setInitiatingAndReceiving(Reference reference) {
    return setInitiatingPerson(reference).setReceivingPerson(reference);
  }

  public EvaluateParametersBuilder setUserType(UserType userType) {
    Preconditions.checkArgument(
        VALID_INITIATING_PERSON_TYPES.contains(userType.getValue()),
        "User type must be one of " + VALID_INITIATING_PERSON_TYPES);

    addUniqueParameter(SystemConstants.USERTYPE)
        .setValue(userType.toCodeableConcept());

    return this;
  }

  public EvaluateParametersBuilder setRecipientType(UserType recipientType) {
    Preconditions.checkArgument(
        VALID_RECEIVING_PERSON_TYPES.contains(recipientType.getValue()),
        "Recipient type must be one of " + VALID_RECEIVING_PERSON_TYPES);

    addUniqueParameter(SystemConstants.RECIPIENTTYPE)
        .setValue(recipientType.toCodeableConcept());

    return this;
  }

  public EvaluateParametersBuilder setUserLanguage(CodeDTO languageDTO) {
    CodeableConcept language =
        Language.fromCode(languageDTO.getCode()).toCodeableConcept();

    addUniqueParameter(SystemConstants.USERLANGUAGE)
        .setValue(language);

    return this;
  }

  public EvaluateParametersBuilder setRecipientLanguage(CodeDTO languageDTO) {
    CodeableConcept language =
        Language.fromCode(languageDTO.getCode()).toCodeableConcept();

    addUniqueParameter(SystemConstants.RECIPIENTLANGUAGE)
        .setValue(language);

    return this;
  }

  public EvaluateParametersBuilder setUserTaskContext(CodeDTO contextDTO) {
    CodeableConcept context = toSnomedCode(contextDTO);

    addUniqueParameter(SystemConstants.USERTASKCONTEXT)
        .setValue(context);

    return this;
  }

  public EvaluateParametersBuilder setSetting(CodeDTO settingDTO) {
    CodeableConcept setting = Setting.fromCode(settingDTO.getCode()).toCodeableConcept();

    addUniqueParameter(SystemConstants.SETTING)
        .setValue(setting);

    return this;
  }

  public EvaluateParametersBuilder addInputData(Resource resource) {
    addParameter(SystemConstants.INPUT_DATA)
        .setResource(resource);

    return this;
  }

  public EvaluateParametersBuilder addInputData(Reference reference) {
    addParameter(SystemConstants.INPUT_DATA)
        .setValue(reference);

    return this;
  }

  public ParametersParameterComponent getUnique(String name) {
    return Iterables.getOnlyElement(parameterNames.get(name));
  }
}
