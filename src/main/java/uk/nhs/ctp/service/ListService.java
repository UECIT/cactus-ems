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
import org.hl7.fhir.dstu3.model.ListResource.ListStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.enums.ListOrder;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Service
@RequiredArgsConstructor
public class ListService {

  private final CaseRepository caseRepository;
  private final ReferenceService referenceService;

  @Transactional
  public ListResource buildFromCase(long caseId) {
    Cases caseEntity = caseRepository.findOne(caseId);

    ListResource listResource = new ListResource();
    listResource.setStatus(ListStatus.CURRENT)
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
    addQuestionnaireResponses(caseEntity, entityTimeList);
    addObservations(caseEntity, entityTimeList);
    addMedications(caseEntity, entityTimeList);
    addImmunizations(caseEntity, entityTimeList);
    addCarePlans(caseEntity, entityTimeList);
    addReferralRequest(caseEntity, entityTimeList);

    entityTimeList.stream()
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

  private void addImmunizations(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> immunizationRefs = caseEntity.getImmunizations().stream()
        .map(imm -> Pair.of(imm.getDateCreated(),
            referenceService.buildRef(ResourceType.Immunization, imm.getId())))
        .collect(Collectors.toUnmodifiableList());

    dateRefList.addAll(immunizationRefs);
  }

  private void addMedications(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> medRefs = caseEntity.getMedications().stream()
        .map(med -> Pair.of(med.getDateCreated(),
            referenceService.buildRef(ResourceType.Medication, med.getId())))
        .collect(Collectors.toUnmodifiableList());

    dateRefList.addAll(medRefs);
  }

  private void addReferralRequest(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    ReferralRequestEntity referralRequestEntity = caseEntity.getReferralRequest();
    dateRefList.add(Pair.of(
        referralRequestEntity.getDateCreated(),
        referenceService.buildRef(ResourceType.ReferralRequest, referralRequestEntity.getId())));
  }

  private void addCarePlans(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> carePlanRefs = caseEntity.getCarePlans().stream()
        .map(carePlan -> Pair.of(carePlan.getDateCreated(), new Reference(carePlan.getReference())))
        .collect(Collectors.toUnmodifiableList());
    dateRefList.addAll(carePlanRefs);
  }

  private void addQuestionnaires(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> dateRefPairs = caseEntity.getQuestionResponses().stream()
        .map(qr -> Pair.of(qr.getDateCreated(), new Reference(qr.getQuestionnaireId())))
        .collect(Collectors.toUnmodifiableList());
    dateRefList.addAll(dateRefPairs);
  }

  private void addQuestionnaireResponses(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> qrRefs = caseEntity.getQuestionResponses().stream()
        .map(qr -> Pair.of(qr.getDateCreated(), new Reference(qr.getReference())))
        .collect(Collectors.toUnmodifiableList());
    dateRefList.addAll(qrRefs);
  }

  private void addObservations(Cases caseEntity, List<Pair<Date, Reference>> dateRefList) {
    List<Pair<Date, Reference>> obsRefs = caseEntity.getObservations().stream()
        .map(obs -> Pair.of(obs.getDateCreated(),
            referenceService.buildRef(ResourceType.Observation, obs.getId())))
        .collect(Collectors.toUnmodifiableList());
    dateRefList.addAll(obsRefs);
  }


}
