package uk.nhs.ctp.service;

import static java.util.Map.Entry.comparingByKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.ListResource.ListEntryComponent;
import org.hl7.fhir.dstu3.model.ListResource.ListMode;
import org.hl7.fhir.dstu3.model.ListResource.ListStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.enums.ListCode;
import uk.nhs.ctp.enums.ListOrder;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Service
@RequiredArgsConstructor
public class ListService {

  private final CaseRepository caseRepository;
  private final ReferenceService referenceService;
  private final ReferralRequestService referralRequestService;
  private final CarePlanService carePlanService;
  private final TokenAuthenticationService authService;

  @Transactional
  public ListResource buildFromCase(long caseId) {
    Cases caseEntity = caseRepository
        .getOneByIdAndSupplierId(caseId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);

    ListResource listResource = new ListResource();
    listResource.setStatus(ListStatus.CURRENT)
        .setMode(ListMode.WORKING)
        .setCode(ListCode.TRIAGE.toCodeableConcept())
        .setStatus(ListStatus.CURRENT)
        .setEncounter(referenceService.buildRef(ResourceType.Encounter, caseId))
        .setSubject(new Reference(caseEntity.getPatientId()))
        .setDate(caseEntity.getClosedDate())
        .setSource(referenceService.buildRef(ResourceType.Device, EMSDeviceService.MAIN_ID))
        .setOrderedBy(ListOrder.EVENT_DATE.toCodeableConcept());


    /* Resources not timestamped */
    addPractitioner(caseEntity, listResource);

    /* Timestamped Resources */
    List<Pair<Date, Reference>> entityTimeList = new ArrayList<>();
    addQuestionnaires(caseEntity, entityTimeList);
    addTriageState(caseEntity, entityTimeList);
    addCarePlans(caseEntity, entityTimeList);
    addReferralRequest(caseEntity, entityTimeList);

    entityTimeList.stream()
        .filter(dateReferencePair -> dateReferencePair.getKey()
            != null) //Filter out old data that was not timestamped.
        .sorted(comparingByKey())
        .map(Pair::getValue)
        .map(ListEntryComponent::new)
        .forEach(listResource::addEntry);

    return listResource;
  }

  private void addPractitioner(Cases caseEntity, ListResource listResource) {
    Reference practitionerRef = referenceService
        .buildRef(ResourceType.Practitioner, caseEntity.getPractitionerId());

    listResource.addEntry(new ListEntryComponent(practitionerRef));
  }

  private void addReferralRequest(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    referralRequestService.getByCaseId(caseEntity.getId())
        .forEach(referralRequest -> dateRefList.add(Pair.of(
            referralRequest.getAuthoredOn(),
            new Reference(referralRequest))));
  }

  private void addCarePlans(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> carePlanRefs = carePlanService.getByCaseId(caseEntity.getId())
        .stream()
        .map(carePlan -> Pair
            .of(new Date(), new Reference(carePlan))) //Todo: what should this be sorted by.
        .collect(Collectors.toList());
    dateRefList.addAll(carePlanRefs);
  }

  private void addQuestionnaires(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> dateRefPairs = caseEntity.getQuestionResponses().stream()
        .map(qr -> Pair.of(qr.getDateCreated(), new Reference(qr.getQuestionnaireId())))
        .collect(Collectors.toUnmodifiableList());
    dateRefList.addAll(dateRefPairs);
  }

  private void addTriageState(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> paramRefs = caseEntity.getParameters().stream()
        .filter(caseParameter -> !caseParameter.isDeleted())
        .map(param -> Pair.of(param.getTimestamp(), new Reference(param.getReference())))
        .collect(Collectors.toUnmodifiableList());
    dateRefList.addAll(paramRefs);
  }

}
