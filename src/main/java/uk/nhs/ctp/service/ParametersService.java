package uk.nhs.ctp.service;

import static uk.nhs.ctp.utils.ResourceProviderUtils.getParameterAsResource;
import static uk.nhs.ctp.utils.ResourceProviderUtils.getParameterByName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationStatus;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.exceptions.FHIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.QuestionResponse;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.attachment.AttachmentService;
import uk.nhs.ctp.service.builder.CareConnectPatientBuilder;
import uk.nhs.ctp.service.builder.RelatedPersonBuilder;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.service.factory.ReferenceStorageServiceFactory;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@AllArgsConstructor
public class ParametersService {

  private static final Logger LOG = LoggerFactory.getLogger(ParametersService.class);

  private CaseRepository caseRepository;
  private CareConnectPatientBuilder careConnectPatientBuilder;
  private RelatedPersonBuilder relatedPersonBuilder;
  private ReferenceStorageServiceFactory storageServiceFactory;
  private AttachmentService attachmentService;

  Parameters getEvaluateParameters(
      Long caseId,
      TriageQuestion[] questionResponse,
      SettingsDTO settings,
      Boolean amending,
      ReferencingContext referencingContext,
      String questionnaireId) {

    var storageService = storageServiceFactory.load(referencingContext);

    Cases caseEntity = caseRepository.findOne(caseId);
    ErrorHandlingUtils.checkEntityExists(caseEntity, "Case");

    Parameters parameters = new Parameters();
    parameters.setMeta(new Meta().addProfile(SystemURL.SERVICE_DEFINITION_EVALUATE));

    List<QuestionnaireResponse> questionnaireResponses = getExistingResponses(
        parameters, caseEntity, storageService);

    setRequestId(caseId, parameters);

    try {
      parameters.addParameter().setName(SystemConstants.PATIENT)
          .setResource(careConnectPatientBuilder.build(caseEntity, storageService));
    } catch (FHIRException e) {
      LOG.error("Cannot parse gender code", e);
      throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot parse patient gender code",
          e);
    }

    setObservations(caseEntity, parameters);
    setContext(caseEntity, parameters);
    saveQuestionnaireResponse(questionResponse, parameters, amending,
        storageService, questionnaireId, caseEntity, questionnaireResponses);
    addObservationInputData(caseEntity, parameters);
    addImmunizationInputData(caseEntity, parameters);
    addMedicationInputData(caseEntity, parameters);

    // Add extra parameters
    addParameterInputData(caseEntity, parameters);

    // set missing parameters
    // userType, userLanguage, userTaskContext,
    // receivingPerson, initiatingPerson,
    // recipientType, recipientLanguage, setting
    setUserType(parameters, settings);
    setUserLanguage(caseEntity, parameters, settings);
    setUserTaskContext(caseEntity, parameters, settings);
    try {
      setInitiatingPerson(parameters, settings);
      setReceivingPerson(parameters, settings);
    } catch (FHIRException e) {
      LOG.error("Cannot parse gender code", e);
      throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot parse person gender code",
          e);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    setRecipientType(caseEntity, parameters, settings);
    setRecipientLanguage(caseEntity, parameters, settings);
    setSetting(caseEntity, parameters, settings);

    return parameters;
  }

  private List<QuestionnaireResponse> getExistingResponses(Parameters parameters, Cases caseEntity,
      ReferenceStorageService storageService) {

    // Get reference to all questionnaire responses for this case
    if (caseEntity.getQuestionResponses() == null) {
      return null;
    }

    List<String> qrReferences = caseEntity.getQuestionResponses().stream()
        .map(QuestionResponse::getReference)
        .collect(Collectors.toUnmodifiableList());

    return storageService
        .findResources(qrReferences, QuestionnaireResponse.class);
  }

  // Adjust to get actual Person.
  private void setInitiatingPerson(
      Parameters parameters,
      SettingsDTO settings)
      throws FHIRException, ParseException {

    var names = new ArrayList<HumanName>();
    var telecom = new ArrayList<ContactPoint>();

    names.add(new HumanName().setFamily(settings.getInitiatingPerson().getName().split(" ")[1])
        .addGiven(settings.getInitiatingPerson().getName().split(" ")[0]));
    telecom.add(new ContactPoint().setSystem(ContactPointSystem.PHONE)
        .setValue(settings.getInitiatingPerson().getTelecom()));

    var birthDate = new SimpleDateFormat("yyyy-MM-dd")
        .parse(settings.getInitiatingPerson().getBirthDate());
    var person = new Person().setName(names)
        .setTelecom(telecom)
        .setGender(AdministrativeGender.fromCode(settings.getInitiatingPerson().getGender()))
        .setBirthDate(birthDate);
    parameters.addParameter()
        .setName(SystemConstants.INITIATINGPERSON)
        .setResource(person);
  }

  private void setUserType(Parameters parameters, SettingsDTO settings) {
    CodeableConcept codeableConcept = new CodeableConcept();

    codeableConcept.setText(settings.getUserType().getDisplay()).addCoding()
        .setCode(settings.getUserType().getCode()).setDisplay(settings.getUserType().getDisplay())
        .setSystem(SystemURL.SNOMED);

    parameters.addParameter().setName(SystemConstants.USERTYPE).setValue(codeableConcept);
  }

  private void setUserLanguage(Cases caseEntity, Parameters parameters, SettingsDTO settings) {
    CodeableConcept codeableConcept = new CodeableConcept();

    codeableConcept.setText(settings.getUserLanguage().getDisplay()).addCoding()
        .setCode(settings.getUserLanguage().getCode())
        .setDisplay(settings.getUserLanguage().getDisplay())
        .setSystem(SystemURL.DATA_DICTIONARY);

    parameters.addParameter().setName(SystemConstants.USERLANGUAGE).setValue(codeableConcept);
  }

  private void setUserTaskContext(Cases caseEntity, Parameters parameters, SettingsDTO settings) {
    CodeableConcept codeableConcept = new CodeableConcept();

    codeableConcept.setText(settings.getUserTaskContext().getDisplay()).addCoding()
        .setCode(settings.getUserTaskContext().getCode())
        .setDisplay(settings.getUserTaskContext().getDisplay())
        .setSystem(SystemURL.SNOMED);

    parameters.addParameter().setName(SystemConstants.USERTASKCONTEXT).setValue(codeableConcept);
  }

  private void setReceivingPerson(
      Parameters parameters,
      SettingsDTO settings)
      throws FHIRException, ParseException {

    var names = new ArrayList<HumanName>();
    var telecom = new ArrayList<ContactPoint>();

    names.add(new HumanName().setFamily(settings.getReceivingPerson().getName().split(" ")[1])
        .addGiven(settings.getReceivingPerson().getName().split(" ")[0]));
    telecom.add(new ContactPoint().setSystem(ContactPointSystem.PHONE)
        .setValue(settings.getReceivingPerson().getTelecom()));

    var birthDate = new SimpleDateFormat("yyyy-MM-dd")
        .parse(settings.getReceivingPerson().getBirthDate());
    var person = new Person().setName(names)
        .setTelecom(telecom)
        .setGender(AdministrativeGender.fromCode(settings.getReceivingPerson().getGender()))
        .setBirthDate(birthDate);
    parameters.addParameter()
        .setName(SystemConstants.RECEIVINGPERSON)
        .setResource(person);
  }

  private void setRecipientType(Cases caseEntity, Parameters parameters, SettingsDTO settings) {
    CodeableConcept codeableConcept = new CodeableConcept();

    codeableConcept.setText(settings.getRecipientType().getDisplay()).addCoding()
        .setCode(settings.getRecipientType().getCode())
        .setDisplay(settings.getRecipientType().getDisplay())
        .setSystem(SystemURL.SNOMED);

    parameters.addParameter().setName(SystemConstants.RECIPIENTTYPE).setValue(codeableConcept);
  }

  private void setRecipientLanguage(Cases caseEntity, Parameters parameters, SettingsDTO settings) {
    CodeableConcept codeableConcept = new CodeableConcept();

    codeableConcept.setText(settings.getRecipientLanguage().getDisplay()).addCoding()
        .setCode(settings.getRecipientLanguage().getCode())
        .setDisplay(settings.getRecipientLanguage().getDisplay())
        .setSystem(SystemURL.DATA_DICTIONARY);

    parameters.addParameter().setName(SystemConstants.RECIPIENTLANGUAGE).setValue(codeableConcept);
  }

  private void setSetting(Cases caseEntity, Parameters parameters, SettingsDTO settings) {
    CodeableConcept codeableConcept = new CodeableConcept();

    codeableConcept.setText(settings.getSetting().getDisplay()).addCoding()
        .setCode(settings.getSetting().getCode())
        .setDisplay(settings.getSetting().getDisplay()).setSystem(SystemURL.SNOMED);

    parameters.addParameter().setName(SystemConstants.SETTING).setValue(codeableConcept);
  }

  private void addImmunizationInputData(Cases caseEntity, Parameters parameters) {
    if (!caseEntity.getImmunizations().isEmpty()) {
      for (CaseImmunization immunizationEntity : caseEntity.getImmunizations()) {
        Immunization immunization = new Immunization()
            .setStatus(Immunization.ImmunizationStatus.COMPLETED)
            .setVaccineCode(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED,
                immunizationEntity.getCode(), immunizationEntity.getDisplay())))
            .setNotGiven(immunizationEntity.getNotGiven());
        parameters.addParameter().setName(SystemConstants.INPUT_DATA).setResource(immunization);
      }
    }
  }

  private void addMedicationInputData(Cases caseEntity, Parameters parameters) {
    if (!caseEntity.getMedications().isEmpty()) {
      for (CaseMedication medicationEntity : caseEntity.getMedications()) {
        MedicationAdministration medication = new MedicationAdministration()
            .setStatus(MedicationAdministrationStatus.COMPLETED)
            .setMedication(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED,
                medicationEntity.getCode(), medicationEntity.getDisplay())))
            .setNotGiven(medicationEntity.getNotGiven());
        parameters.addParameter().setName(SystemConstants.INPUT_DATA).setResource(medication);
      }
    }
  }

  private void addObservationInputData(Cases caseEntity, Parameters parameters) {
    if (!caseEntity.getObservations().isEmpty()) {
      for (CaseObservation observationEntity : caseEntity.getObservations()) {
        Observation observation = new CareConnectObservation()
            .setStatus(Observation.ObservationStatus.FINAL)
            .setCode(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED,
                observationEntity.getCode(), observationEntity.getDisplay())))
            .setValue(new BooleanType(observationEntity.getValue()));

        if (observationEntity.getDataAbsentCode() != null
            && observationEntity.getDataAbsentDisplay() != null) {
          observation
              .setDataAbsentReason(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED,
                  observationEntity.getDataAbsentCode(),
                  observationEntity.getDataAbsentDisplay())));
        }

        parameters.addParameter().setName(SystemConstants.INPUT_DATA).setResource(observation);
      }
    }
  }

  private void addParameterInputData(Cases caseEntity, Parameters parameters) {
    if (!caseEntity.getParameters().isEmpty()) {
      for (CaseParameter parameterEntity : caseEntity.getParameters()) {
        ParametersParameterComponent parameter = new ParametersParameterComponent();
        parameter.setName(parameterEntity.getName());
        parameter.setValue(new StringType(parameterEntity.getValue()));
        parameters.addParameter(parameter);
      }
    }
  }

  private Type getAnswerValue(TriageQuestion triageQuestion) {
    switch (triageQuestion.getQuestionType().toUpperCase()) {
      case "STRING":
      case "TEXT":
        return new StringType(triageQuestion.getResponseString());
      case "INTEGER":
        return new IntegerType(triageQuestion.getResponseInteger());
      case "BOOLEAN":
        return new BooleanType(triageQuestion.getResponseBoolean());
      case "DECIMAL":
        return new DecimalType(triageQuestion.getResponseDecimal());
      case "DATE":
        return new DateTimeType(triageQuestion.getResponseDate());
      case "ATTACHMENT":
        var attachmentData = triageQuestion.getResponseAttachment().getBytes();
        String attachmentType = triageQuestion.getResponseAttachmentType();
        return attachmentService.storeAttachment(
            MediaType.valueOf(attachmentType), attachmentData);
      case "REFERENCE":
        if (isImageMapAnswer(triageQuestion)) {
          CoordinateResource coordinateResource = new CoordinateResource();
          coordinateResource.setXCoordinate(new IntegerType(triageQuestion.getResponseCoordinates().getX()));
          coordinateResource.setYCoordinate(new IntegerType(triageQuestion.getResponseCoordinates().getY()));
          return new Reference(coordinateResource);
        }
      default:
        return new Coding()
            .setCode(triageQuestion.getResponse().getCode())
            .setDisplay(triageQuestion.getResponse().getDisplay());
    }
  }

  private boolean isImageMapAnswer(TriageQuestion triageQuestion) {
    return triageQuestion.getExtension()
        .getCode().equals("imagemap");
  }

  private void saveQuestionnaireResponse(
      TriageQuestion[] questionResponse,
      Parameters parameters,
      Boolean amending,
      ReferenceStorageService storageService, String questionnaireId,
      Cases caseEntity,
      List<QuestionnaireResponse> questionnaireResponses) {
    if (questionResponse != null) {
      QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse()
          .setQuestionnaire(new Reference(new IdType(SystemConstants.QUESTIONNAIRE,
              questionnaireId.replace("#", ""))));

      for (TriageQuestion triageQuestion : questionResponse) {
        questionnaireResponse.addItem()
            .setLinkId(triageQuestion.getQuestionId())
            .setText(triageQuestion.getQuestion())
            .addAnswer().setValue(getAnswerValue(triageQuestion));
      }

      var inputParameters = getParameterAsResource(
          parameters.getParameter(),
          SystemConstants.INPUT_PARAMETERS,
          Parameters.class);
      var context = getParameterByName(inputParameters.getParameter(), SystemConstants.CONTEXT);
      var partyComponent = getParameterByName(context.getPart(), SystemConstants.PARTY);

      var patient = partyComponent.getValue().primitiveValue().equals("1")
          ? getParameterAsResource(parameters.getParameter(), SystemConstants.PATIENT)
          : relatedPersonBuilder.build();

      questionnaireResponse.setSource(storageService.store(patient));

      var qr = questionnaireResponses.stream()
          .filter(equalQuestionnaireIds(questionnaireId))
          .findFirst();

      if (qr.isPresent() && amending) {
        qr.orElseThrow(IllegalStateException::new)
            .setStatus(QuestionnaireResponseStatus.AMENDED)
            .setItem(questionnaireResponse.getItem());

        storageService.updateExternal(qr.get());
      } else if (qr.isEmpty()) {
        questionnaireResponse.setStatus(QuestionnaireResponseStatus.COMPLETED);
        Reference qrRef = storageService.storeExternal(questionnaireResponse);
        QuestionResponse questionResponseEntity = QuestionResponse.builder()
            .reference(qrRef.getResource().getIdElement().getValue())
            .questionnaireId(questionnaireId)
            .build();
        caseEntity.addQuestionResponse(questionResponseEntity);
        caseRepository.save(caseEntity);
        questionnaireResponses.add(questionnaireResponse);
      }

      questionnaireResponses.forEach(
          resource -> parameters.addParameter().setName(SystemConstants.INPUT_DATA)
              .setResource(resource));
    }
  }

  private Predicate<QuestionnaireResponse> equalQuestionnaireIds(String questionnaireId) {
    return resp -> resp.getQuestionnaire().getReference().split("/")[1]
        .equals(questionnaireId);
  }

  private void setContext(Cases caseEntity, Parameters parameters) {
    ParametersParameterComponent inputParameters = parameters.addParameter()
        .setName(SystemConstants.INPUT_PARAMETERS);
    Parameters inputParamsResource = new Parameters();

    inputParamsResource.addParameter().setName(SystemConstants.CONTEXT)
        .addPart(new ParametersParameterComponent().setName(SystemConstants.PARTY)
            .setValue(new StringType(caseEntity.getParty().getCode())))
        .addPart(new ParametersParameterComponent().setName(SystemConstants.SKILLSET)
            .setValue(new StringType(caseEntity.getSkillset().getCode())));

    inputParameters.setResource(inputParamsResource);
  }

  private void setObservations(Cases caseEntity, Parameters parameters) throws FHIRException {

    Observation genderObservation = new CareConnectObservation()
        .setStatus(Observation.ObservationStatus.FINAL)
        .setCode(
            new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED, "263495000", "Gender")))
        .setValue(new StringType(caseEntity.getGender()));
    parameters.addParameter().setName(SystemConstants.INPUT_DATA).setResource(genderObservation);

    Observation ageObservation = new CareConnectObservation()
        .setStatus(Observation.ObservationStatus.FINAL)
        .setCode(new CodeableConcept().addCoding(new Coding(SystemURL.SNOMED, "397669002", "Age")))
        .setValue(new StringType(caseEntity.getDateOfBirth().toString()));
    parameters.addParameter().setName(SystemConstants.INPUT_DATA).setResource(ageObservation);
  }

  private void setRequestId(Long caseId, Parameters parameters) {
    parameters.addParameter().setName(SystemConstants.REQUEST_ID).setValue(new IdType(caseId));
    // parameters.addParameter().setName(SystemConstants.REQUEST_ID).setValue(new
    // StringType(String.valueOf(caseId)));
  }
}
