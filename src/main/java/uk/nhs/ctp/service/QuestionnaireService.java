package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CareConnectRelatedPerson;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.EncounterEntity;
import uk.nhs.ctp.entities.QuestionResponse;
import uk.nhs.ctp.repos.EncounterRepository;
import uk.nhs.ctp.service.attachment.AttachmentService;
import uk.nhs.ctp.service.builder.ReferenceBuilder;
import uk.nhs.ctp.service.builder.RelatedPersonBuilder;
import uk.nhs.ctp.service.dto.TriageQuestion;

@Service
@AllArgsConstructor
public class QuestionnaireService {

  private EncounterRepository encounterRepository;
  private AttachmentService attachmentService;
  private StorageService storageService;
  private RelatedPersonBuilder relatedPersonBuilder;

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
          coordinateResource
              .setXCoordinate(new IntegerType(triageQuestion.getResponseCoordinates().getX()));
          coordinateResource
              .setYCoordinate(new IntegerType(triageQuestion.getResponseCoordinates().getY()));
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

  @Nonnull
  private List<QuestionnaireResponse> getExistingResponses(EncounterEntity caseEntity) {

    // Get reference to all questionnaire responses for this case
    if (caseEntity.getQuestionResponses() == null) {
      return new ArrayList<>();
    }

    List<String> qrReferences = caseEntity.getQuestionResponses().stream()
        .map(QuestionResponse::getReference)
        .collect(Collectors.toUnmodifiableList());

    return storageService
        .findResources(qrReferences, QuestionnaireResponse.class);
  }

  /**
   * Saves or updates the current questionnaire response
   *
   * @return List of all persisted {@link QuestionnaireResponse}s for this encounter
   */
  public List<QuestionnaireResponse> updateEncounterResponses(
      EncounterEntity caseEntity, String questionnaireId, TriageQuestion[] questionResponse,
      Boolean amending, ReferenceBuilder referenceBuilder) {

    List<QuestionnaireResponse> questionnaireResponses = getExistingResponses(caseEntity);

    if (questionResponse != null) {

      // Build FHIR response object from TriageQuestion DTOs
      QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse()
          .setQuestionnaire(new Reference(new IdType(ResourceType.Questionnaire.name(),
              questionnaireId.replace("#", ""))));

      for (TriageQuestion triageQuestion : questionResponse) {
        questionnaireResponse.addItem()
            .setLinkId(triageQuestion.getQuestionId())
            .setText(triageQuestion.getQuestion())
            .addAnswer().setValue(getAnswerValue(triageQuestion));
      }

      // Select 1st or 3rd party source
      if (caseEntity.getParty().getCode().equals("1")) {
        questionnaireResponse.setSource(new Reference(caseEntity.getPatientId()));
      } else {
        // TODO replace with referenced resource (this will be contained)
        CareConnectRelatedPerson relatedPerson = relatedPersonBuilder.build();
        questionnaireResponse.setSource(referenceBuilder.getReference(relatedPerson));
      }

      // Look for an existing response for this questionnaire
      var qr = questionnaireResponses.stream()
          .filter(equalQuestionnaireIds(questionnaireId))
          .findFirst();

      if (qr.isPresent() && amending) {

        // Amend existing response
        qr.get()
            .setStatus(QuestionnaireResponseStatus.AMENDED)
            .setItem(questionnaireResponse.getItem());

        storageService.updateExternal(qr.get());

      } else if (qr.isEmpty()) {

        // Create new response
        questionnaireResponse.setStatus(QuestionnaireResponseStatus.COMPLETED);
        String qrRef = storageService.storeExternal(questionnaireResponse);

        QuestionResponse questionResponseEntity = QuestionResponse.builder()
            .reference(qrRef)
            .questionnaireId(questionnaireId)
            .build();

        caseEntity.addQuestionResponse(questionResponseEntity);
        encounterRepository.save(caseEntity);
        questionnaireResponses.add(questionnaireResponse);
      }

    }
    return questionnaireResponses;
  }

  private Predicate<QuestionnaireResponse> equalQuestionnaireIds(String questionnaireId) {
    return resp -> new IdType(resp.getQuestionnaire().getReference())
        .getIdPart().equals(questionnaireId);
  }
}
