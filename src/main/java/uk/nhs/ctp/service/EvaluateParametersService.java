package uk.nhs.ctp.service;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.enums.Setting;
import uk.nhs.ctp.enums.UserType;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.builder.EvaluateParametersBuilder;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Service
@AllArgsConstructor
@Slf4j
public class EvaluateParametersService {

  private final CaseRepository caseRepository;
  private final ReferenceService referenceService;
  private final QuestionnaireService questionnaireService;
  private final TokenAuthenticationService authService;
  private final GenericResourceLocator resourceLocator;

  Parameters getEvaluateParameters(CdssRequestDTO requestDetails, CdssSupplier cdssSupplier,
      String requestId) {
    Long caseId = requestDetails.getCaseId();
    SettingsDTO settings = requestDetails.getSettings();
    Cases caseEntity = caseRepository
        .getOneByIdAndSupplierId(caseId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);
    String patientId = caseEntity.getPatientId();

    EvaluateParametersBuilder builder = new EvaluateParametersBuilder()
        .setRequestId(requestId)
        .setEncounter(referenceService.buildRef(ResourceType.Encounter, caseId))
        .setPatient(new Reference(patientId))
        .setSetting(settings.getSetting());

    addPeople(builder, patientId, settings);
    addInputData(caseEntity, builder, cdssSupplier.getInputDataRefType());
    addQuestionnaireResponses(
        builder,
        caseEntity,
        requestDetails.getQuestionnaireId(),
        requestDetails.getQuestionResponse(),
        requestDetails.getAmendingPrevious(),
        cdssSupplier.getBaseUrl()
    );

    return builder.build();
  }

  private void addInputData(Cases caseEntity, EvaluateParametersBuilder builder, ReferencingType inputDataRefType) {
    List<Reference> references = caseEntity.getParameters().stream()
        .filter(caseParameter -> !caseParameter.isDeleted())
        .map(CaseParameter::getReference)
        .map(Reference::new)
        .collect(Collectors.toUnmodifiableList());

    switch (defaultIfNull(inputDataRefType, ReferencingType.BY_REFERENCE)) {
      case BY_REFERENCE:
        references.forEach(builder::addInputData);
        break;
      case BY_RESOURCE:
        references.stream()
            .map(reference -> (Resource)resourceLocator.findResource(reference))
            .forEach(builder::addInputData);
        break;
    }
  }

  private void addPeople(EvaluateParametersBuilder builder, String patientId, SettingsDTO settings) {

    Setting setting = Setting.fromCode(settings.getSetting().getCode());
    // Settings of phone call or face to face imply the practitioner is the initiating person
    UserType initiatingType = setting == Setting.ONLINE
        ? UserType.fromCode(settings.getUserType().getCode())
        : UserType.PRACTITIONER;

    UserType receivingType = initiatingType != UserType.PRACTITIONER
        ? initiatingType
        : UserType.fromCode(settings.getUserType().getCode());

    Reference patientRef = new Reference(patientId);
    //Assume RelatedPerson.id = patient.id
    Reference relatedPersonRef = referenceService
        .buildRef(ResourceType.RelatedPerson, new IdType(patientId).getIdPart());

    builder
        .setUserType(initiatingType)
        .setRecipientType(receivingType)
        .setUserLanguage(settings.getUserLanguage())
        .setRecipientLanguage(settings.getRecipientLanguage())
        .setUserTaskContext(settings.getUserTaskContext());

    switch (initiatingType) {
      case PATIENT:
        builder.setInitiatingAndReceiving(patientRef);
        break;
      case RELATED_PERSON:
        // TODO get related person from frontend
        builder.setInitiatingAndReceiving(relatedPersonRef);
        break;
      case PRACTITIONER:
        Preconditions.checkNotNull(settings.getPractitioner(), "No practitioner specified");
        builder.setInitiatingPerson(
            referenceService
                .buildRef(ResourceType.Practitioner, settings.getPractitioner().getId()));
        builder.setReceivingPerson(
            UserType.PATIENT.equals(receivingType) ? patientRef : relatedPersonRef);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + initiatingType);
    }
  }

  private void addQuestionnaireResponses(
      EvaluateParametersBuilder builder,
      Cases caseEntity,
      String questionnaireId,
      TriageQuestion[] questionResponse,
      Boolean amending,
      String supplierBaseUrl
  ) {
    Reference responseSource = (Reference) builder.getUnique(SystemConstants.RECEIVINGPERSON)
        .getValue();
    List<QuestionnaireResponse> questionnaireResponses = questionnaireService
        .updateEncounterResponses(
            caseEntity, questionnaireId, questionResponse, amending, responseSource,
            supplierBaseUrl);
    builder.addQuestionnaireResponses(questionnaireResponses);
  }
}
