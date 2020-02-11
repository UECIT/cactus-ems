package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.google.common.base.Preconditions;
import java.util.List;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CaseCarePlan;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.EncounterService;
import uk.nhs.ctp.service.GenericResourceLocator;
import uk.nhs.ctp.service.ReferenceService;

@Component
@AllArgsConstructor
public class EncounterProvider implements IResourceProvider {

  private GenericResourceLocator resourceLocator;
  private EncounterService encounterService;
  private ReferenceService referenceService;
  private CaseRepository caseRepository;


  @Operation(name = "$UEC-Report", idempotent = true, type = Encounter.class)
  @Transactional
  public Bundle getEncounterReport(@IdParam IdType encounterId) {

    Bundle bundle = new Bundle();
    bundle.setType(BundleType.DOCUMENT);
    Long caseId = encounterId.getIdPartAsLong();

    addEncounter(bundle, caseId);
    addReferralRequest(bundle, caseId);
    addObservations(bundle, caseId);
    addCarePlans(bundle, caseId);

    return bundle;
  }

  /**
   * Dereference a resource (potentially external) and add to bundle
   *
   * @param bundle    the output bundle
   * @param reference the resource to fetch
   * @param parentId  fetch resource relative to this parent
   */
  private <T extends Resource> T addResource(Bundle bundle, Reference reference, IIdType parentId) {

    if (parentId != null && parentId.hasBaseUrl()) {
      referenceService.resolve(parentId.getBaseUrl(), reference);
    }

    String fullUrl = referenceService.buildId(reference.getReferenceElement());

    @SuppressWarnings("unchecked")
    T resource = reference.getResource() != null ?
        (T) reference.getResource() :
        resourceLocator.findResource(fullUrl);

    bundle.addEntry()
        .setFullUrl(fullUrl)
        .setResource(resource);
    return resource;
  }

  private void addReferralRequest(Bundle bundle, Long caseId) {
    encounterService.getReferralRequestForEncounter(caseId)
        .ifPresent(referralRequest -> {
          bundle.addEntry()
              .setFullUrl(
                  referenceService.buildId(ResourceType.ReferralRequest, referralRequest.getId()))
              .setResource(referralRequest);

          // Dereference primary concern (Conditions)
          referralRequest.getReasonReference()
              .forEach(reference -> addResource(bundle, reference, referralRequest.getIdElement()));

          // Dereference secondary concerns (Conditions)
          referralRequest.getSupportingInfo()
              .forEach(reference -> addResource(bundle, reference, referralRequest.getIdElement()));
        });
  }

  private Encounter addEncounter(Bundle bundle, Long caseId) {
    Encounter encounter = encounterService.getEncounter(caseId);
    String encounterRefString = referenceService.buildId(ResourceType.Encounter, caseId);
    bundle.addEntry(new BundleEntryComponent()
        .setFullUrl(encounterRefString)
        .setResource(encounter));

    // Add patient
    addResource(bundle, encounter.getSubject(), encounter.getIdElement());
    return encounter;
  }

  private void addObservations(Bundle bundle, Long caseId) {
    List<Observation> observations = encounterService.getObservationsForEncounter(caseId);
    observations.stream()
        .map(obs -> new BundleEntryComponent()
            .setFullUrl(referenceService
                .buildId(ResourceType.Observation, obs.getIdElement().getIdPartAsLong()))
            .setResource(obs))
        .forEach(bundle::addEntry);
  }

  @Transactional
  public void addCarePlans(Bundle bundle, Long caseId) {
    Cases caseEntity = caseRepository.getOne(caseId);
    for (CaseCarePlan carePlan : caseEntity.getCarePlans()) {
      Reference reference = new Reference(carePlan.getReference());
      Preconditions.checkArgument(reference.getReferenceElement().isAbsolute(),
          "CarePlan must have absolute reference");
      addResource(bundle, reference, null);
    }
  }

  @Read
  public Encounter getEncounter(@IdParam IdType id) {
    return encounterService.getEncounter(id.getIdPartAsLong());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Encounter.class;
  }
}
