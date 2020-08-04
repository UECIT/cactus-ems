package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.QuestionResponse;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.transform.QuestionnaireAnswerValueTransformer;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

  private final CaseRepository caseRepository;
  private final StorageService storageService;
  private final GenericResourceLocator resourceLocator;
  private final ReferenceService referenceService;
  private final NarrativeService narrativeService;
  private final QuestionnaireAnswerValueTransformer answerValueTransformer;

  /**
   * Saves or updates the current questionnaire response
   *
   * @return List of all persisted {@link QuestionnaireResponse}s for this encounter
   */
  public List<QuestionnaireResponse> updateEncounterResponses(
      Cases caseEntity,
      String questionnaireId,
      TriageQuestion[] questionResponse,
      Boolean amending,
      Reference source,
      String supplierBaseUrl) {

    List<QuestionnaireResponse> questionnaireResponses = getExistingResponses(caseEntity);

    if (questionResponse == null) {
      return questionnaireResponses;
    }

    // remove questionId part from questionnaireId if present
    questionnaireId = questionnaireId.split("#")[0];
    var questionnaireRef = referenceService.buildRef(
        supplierBaseUrl,
        ResourceType.Questionnaire,
        questionnaireId);
    var patientRef = referenceService.buildRef(new IdType(caseEntity.getPatientId()));
    QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse()
        .setQuestionnaire(questionnaireRef)
        .setSubject(patientRef)
        .setContext(referenceService.buildRef(ResourceType.Encounter, caseEntity.getId()))
        .setAuthored(new Date());

    Questionnaire questionnaire = resourceLocator.findResource(questionnaireRef);

    for (TriageQuestion triageQuestion : questionResponse) {
      questionnaireResponse.addItem()
          .setSubject(patientRef)
          .setLinkId(triageQuestion.getQuestionId())
          .setText(getQuestionText(questionnaire, triageQuestion.getQuestionId()))
          .addAnswer()
            .setValue(answerValueTransformer.transform(triageQuestion));
    }

    questionnaireResponse.setSource(source);
    questionnaireResponse.setText(buildNarrative(questionnaire, questionResponse));

    // Look for an existing response for this questionnaire
    var qr = questionnaireResponses.stream()
        .filter(equalQuestionnaireIds(questionnaireRef.getReference()))
        .findFirst();

    if (qr.isPresent() && amending) {

      // Amend existing response
      qr.get()
          .setStatus(QuestionnaireResponseStatus.AMENDED)
          .setItem(questionnaireResponse.getItem())
          .setText(questionnaireResponse.getText());

      storageService.updateExternal(qr.get());

    } else if (qr.isEmpty()) {

      // Create new response
      questionnaireResponse.setStatus(QuestionnaireResponseStatus.COMPLETED);
      String qrRef = storageService.storeExternal(questionnaireResponse);

      QuestionResponse questionResponseEntity = QuestionResponse.builder()
          .reference(qrRef)
          .questionnaireId(questionnaireRef.getReference())
          .build();

      caseEntity.addQuestionResponse(questionResponseEntity);
      caseRepository.save(caseEntity);
      questionnaireResponses.add(questionnaireResponse);
    }

    return questionnaireResponses;
  }

  private String getAnswerString(TriageQuestion triageQuestion) {
    switch (QuestionnaireItemType.valueOf(triageQuestion.getQuestionType())) {
      case STRING:
      case TEXT:
        return triageQuestion.getResponseString();
      case INTEGER:
        return triageQuestion.getResponseInteger();
      case BOOLEAN:
        return triageQuestion.getResponseBoolean();
      case DECIMAL:
        return triageQuestion.getResponseDecimal();
      case DATE:
      case DATETIME:
        return triageQuestion.getResponseDate();
      case ATTACHMENT:
        return triageQuestion.getResponseAttachmentType();
      case REFERENCE:
        return triageQuestion.getResponseCoordinates().toString();
      default:
        return triageQuestion.getResponse().getDisplay();
    }
  }

  @Nonnull
  private List<QuestionnaireResponse> getExistingResponses(Cases caseEntity) {

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

  @Value
  private static class AnswerText {
    String question;
    String answer;
  }

  private Narrative buildNarrative(Questionnaire questionnaire, TriageQuestion[] questionResponse) {
    var textPairs = Arrays.stream(questionResponse)
        .map(qr -> new AnswerText(
            "'" + getQuestionText(questionnaire, qr.getQuestionId()) + "'",
            "'" + getAnswerString(qr) + "'"))
        .collect(Collectors.toUnmodifiableList());

    if (textPairs.size() == 0) {
      return narrativeService.buildNarrative("Patient selected no answer");
    }
    if (textPairs.size() == 1) {
      var pair = textPairs.get(0);
      return narrativeService.buildNarrative(
          "Patient answered " + pair.getQuestion() + " with " + pair.getAnswer());
    }

    var allLines = Stream.concat(
        Stream.of("Patient answered the following questions:"),
        textPairs.stream().map(pair -> pair.getQuestion() + " -> " + pair.getAnswer()))
        .collect(Collectors.toUnmodifiableList());

    return narrativeService.buildNarrative(allLines);
  }


  private String getQuestionText(Questionnaire questionnaire, String questionId) {
    return getQuestionnaireItem(questionnaire.getItem(), questionId)
        .map(QuestionnaireItemComponent::getText)
        .orElseThrow();
  }

  private Optional<QuestionnaireItemComponent> getQuestionnaireItem(List<QuestionnaireItemComponent> items, String id) {
    if (ObjectUtils.isEmpty(items)) {
      return Optional.empty();
    }

    List<QuestionnaireItemComponent> collect = items.stream()
        .map(QuestionnaireItemComponent::getItem)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    // Get questions at this level or recursively search at the next one.
    return items.stream()
        .filter(i -> id.equals(i.getLinkId()))
        .findFirst()
        .or(() -> getQuestionnaireItem(collect, id));
  }

  private Predicate<QuestionnaireResponse> equalQuestionnaireIds(String questionnaireId) {
    return resp -> resp.getQuestionnaire().getReference()
        .equals(questionnaireId);
  }
}
