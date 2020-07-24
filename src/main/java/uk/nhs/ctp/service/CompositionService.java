package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Composition.CompositionStatus;
import org.hl7.fhir.dstu3.model.Composition.DocumentConfidentiality;
import org.hl7.fhir.dstu3.model.Composition.SectionComponent;
import org.hl7.fhir.dstu3.model.Composition.SectionMode;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.CompositionEntity;
import uk.nhs.ctp.entities.QuestionResponse;
import uk.nhs.ctp.enums.DocumentSectionCode;
import uk.nhs.ctp.enums.DocumentType;
import uk.nhs.ctp.enums.ListOrder;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.repos.CompositionRepository;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.transform.CompositionEntityTransformer;
import uk.nhs.ctp.transform.CompositionTransformer;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompositionService {

  private final ReferenceService referenceService;
  private final FhirContext fhirContext;
  private final CaseRepository caseRepository;
  private final CompositionRepository compositionRepository;
  private final CompositionEntityTransformer compositionEntityTransformer;
  private final CompositionTransformer compositionTransformer;
  private final GenericResourceLocator resourceLocator;
  private final NarrativeService narrativeService;
  private final ReferralRequestService referralRequestService;
  private final CarePlanService carePlanService;
  private final TokenAuthenticationService authService;

  @Transactional
  public List<Composition> getAllByEncounter(long encounterId) {
    if (!caseRepository.exists(encounterId)) {
      return Collections.emptyList();
    }

    return caseRepository.getOneByIdAndSupplierId(encounterId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound)
        .getCompositions()
        .stream()
        .map(compositionEntityTransformer::transform)
        .collect(Collectors.toUnmodifiableList());
  }

  public Composition get(long compositionId) {
    return compositionEntityTransformer.transform(compositionRepository.findOne(compositionId));
  }

  /**
   * Create or update the active composition associated with a service.
   *
   * @param caseId     The id of the encounter to crupdate compositions for.
   * @param cdssResult The result of $evaluate, contains the data to add to the composition.
   */
  public void crupdate(long caseId, CdssResult cdssResult) {

    var caseEntity = caseRepository.getOneByIdAndSupplierId(caseId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);

    var compositionEntity = caseEntity.getCompositions()
        .stream()
        .filter(c -> !c.isComplete())
        .findFirst()
        .orElseGet(() -> createCompositionEntity(caseEntity));
    var composition = compositionEntityTransformer.transform(compositionEntity);

    if (isFinal(cdssResult)) {
      composition.setStatus(CompositionStatus.FINAL);
      compositionEntity.setComplete(true);
    } else {
      composition.setStatus(CompositionStatus.PRELIMINARY);
    }
    composition.setDate(new Date());

    composition.setText(buildCompositionNarrative(compositionEntity));

    composition.addSection(buildEvaluateSection(cdssResult, caseEntity));

    if (cdssResult.hasResult()) {
      composition.addSection(buildResultSection(cdssResult, caseEntity));
    }

    compositionEntity.setResource(fhirContext.newJsonParser().encodeResourceToString(composition));

    caseRepository.saveAndFlush(caseEntity);
  }

  private Narrative buildCompositionNarrative(CompositionEntity compositionEntity) {
    var statusText = compositionEntity.isComplete() ? "final" : "preliminary";
    var text = "EMS CDSS " + statusText + " encounter details, last updated on " + new Date();
    return narrativeService.buildNarrative(text);
  }

  private CompositionEntity createCompositionEntity(Cases caseEntity) {
    Composition composition = new Composition()
        .setType(DocumentType.REPORT_CLINICAL_ENCOUNTER.toCodeableConcept())
        .setSubject(referenceService.buildRef(ResourceType.Patient, caseEntity.getPatientId()))
        .setEncounter(referenceService.buildRef(ResourceType.Encounter, caseEntity.getId()))
        .addAuthor(referenceService.buildRef(ResourceType.Device, EMSDeviceService.MAIN_ID))
        .setTitle("EMS CDSS Encounter details")
        .setConfidentiality(DocumentConfidentiality.N)
        .setCustodian(referenceService.buildRef(ResourceType.Organization, "self"));

    var entity = compositionTransformer.transform(composition);
    caseEntity.addComposition(entity);
    return entity;
  }

  private SectionComponent buildResultSection(CdssResult cdssResult, Cases caseEntity) {
    var entries = buildResultEntries(cdssResult, caseEntity);

    return new SectionComponent()
        .setTitle(String.format("Result %s", cdssResult.getRequestId()))
        .setCode(DocumentSectionCode.PLAN_AND_REQUESTED_ACTIONS.toCodeableConcept())
        .setMode(SectionMode.CHANGES)
        .setOrderedBy(ListOrder.EVENT_DATE.toCodeableConcept())
        .setEntry(entries)
        .setText(buildSectionNarrative(entries));
  }

  private SectionComponent buildEvaluateSection(CdssResult cdssResult, Cases caseEntity) {
    var entries = buildResponseEntries(cdssResult, caseEntity);

    return new SectionComponent()
        .setTitle(String.format("Request %s", cdssResult.getRequestId()))
        .setCode(DocumentSectionCode.OBSERVATIONS.toCodeableConcept())
        .setMode(SectionMode.CHANGES)
        .setOrderedBy(ListOrder.EVENT_DATE.toCodeableConcept())
        .setEntry(entries)
        .setText(buildSectionNarrative(entries));
  }

  private Narrative buildSectionNarrative(List<Reference> references) {
    return narrativeService.buildCombinedNarrative(references.stream()
        .map(resourceLocator::findResource)
        .map(DomainResource.class::cast)
        .map(DomainResource::getText)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableList()));
  }

  private List<Reference> buildResultEntries(CdssResult cdssResult, Cases caseEntity) {
    if (!cdssResult.hasResult()) {
      return Collections.emptyList();
    }

    return Stream.concat(getReferralRequest(caseEntity).stream(), getCarePlans(caseEntity))
        .collect(Collectors.toUnmodifiableList());
  }

  private List<Reference> buildResponseEntries(CdssResult cdssResult, Cases caseEntity) {

    var references = new ArrayList<Reference>();
    if (StringUtils.isNotEmpty(cdssResult.getQuestionnaireRef())) {
      references.add(new Reference(cdssResult.getQuestionnaireRef()));
    }

    if (!isFinal(cdssResult)) {
      // only add interim referral requests and care plans to the response sections
      Stream.concat(getReferralRequest(caseEntity).stream(), getCarePlans(caseEntity))
          .filter(Objects::nonNull)
          .forEach(references::add);
    }

    // MAYBEDO: only add the latest assertions
    caseEntity.getObservations()
        .stream()
        .map(CaseObservation::getId)
        .map(id -> referenceService.buildRef(ResourceType.Observation, id))
        .forEach(references::add);
    caseEntity.getImmunizations()
        .stream()
        .map(CaseImmunization::getId)
        .map(id -> referenceService.buildRef(ResourceType.Immunization, id))
        .forEach(references::add);
    caseEntity.getMedications()
        .stream()
        .map(CaseMedication::getId)
        .map(id -> referenceService.buildRef(ResourceType.Medication, id))
        .forEach(references::add);
    caseEntity.getQuestionResponses()
        .stream()
        .map(QuestionResponse::getReference)
        .map(ref -> referenceService.buildRef(ResourceType.QuestionnaireResponse, ref))
        .forEach(references::add);

    return references;
  }

  private List<Reference> getReferralRequest(Cases caseEntity) {
    return referralRequestService.getByCaseId(caseEntity.getId()).stream()
        .map(Reference::new)
        .collect(Collectors.toList());
  }

  private Stream<Reference> getCarePlans(Cases caseEntity) {
    return carePlanService.getByCaseId(caseEntity.getId())
        .stream()
        .map(Reference::new);
  }

  private boolean isFinal(CdssResult cdssResult) {
    return cdssResult.hasResult() || cdssResult.hasTrigger();
  }
}
