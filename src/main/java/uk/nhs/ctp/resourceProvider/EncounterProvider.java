package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CaseCarePlan;
import uk.nhs.ctp.service.AppointmentService;
import uk.nhs.ctp.service.CompositionService;
import uk.nhs.ctp.service.EncounterService;
import uk.nhs.ctp.service.ListService;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Component
@AllArgsConstructor
public class EncounterProvider implements IResourceProvider {

  private GenericResourceLocator resourceLocator;
  private EncounterService encounterService;
  private AppointmentService appointmentService;
  private ReferenceService referenceService;
  private ListService listService;
  private CompositionService compositionService;
  private FhirContext context;

  /**
   * Encounter Report Search
   * @param encounterParam id search parameter of the encounter
   * @param revIncludes resources to include that reference this encounter (ignored)
   * @param include resources to include that are referenced by this encounter (ignored)
   * @return Bundle containing the encounter report:
   * <ul>
   *   <li>Encounter</li>
   *   <li>Encounter.subject (Patient)</li>
   *   <li>ReferralRequest</li>
   *   <li>ReferralRequest.reason (Condition)</li>
   *   <li>ReferralRequest.supportingInformation (Condition)</li>
   *   <li>CarePlans</li>
   *   <li>Observations</li>
   *   <li>List</li>
   *   <li>Composition</li>
   * </ul>
   */
  @Search
  public Bundle getEncounterReport(
      @RequiredParam(name = Encounter.SP_RES_ID) TokenParam encounterParam,
      @IncludeParam(reverse = true) Set<Include> revIncludes, //Ignored
      @IncludeParam Set<Include> include //Ignored
  ) {
    Bundle bundle = new Bundle();
    bundle.setType(BundleType.DOCUMENT);
    Long caseId = Long.valueOf(encounterParam.getValue());

    addEncounter(bundle, caseId);
    addReferralRequest(bundle, caseId);
    addObservations(bundle, caseId);
    addCarePlans(bundle, caseId);
    addList(bundle, caseId);
    addCompositions(bundle, caseId);

    return bundle;
  }

  /**
   * Dereference a resource (potentially external) and add to bundle
   *
   * @param bundle    the output bundle
   * @param reference the resource to fetch
   * @param parentId  fetch resource relative to this parent
   */
  private Resource addResource(Bundle bundle, Reference reference, IIdType parentId) {

    if (parentId != null && parentId.hasBaseUrl()) {
      referenceService.resolve(parentId.getBaseUrl(), reference);
    }

    String fullUrl = referenceService.buildId(reference.getReferenceElement());

    Resource resource = reference.getResource() != null ?
        (Resource) reference.getResource() :
        resourceLocator.findResource(fullUrl);

    bundle.addEntry()
        .setFullUrl(fullUrl)
        .setResource(resource);
    return resource;
  }

  private void addReferralRequest(Bundle bundle, Long caseId) {
    encounterService.getReferralRequestForEncounter(caseId)
        .ifPresent(referralRequest -> {
          String url = referenceService
              .buildId(ResourceType.ReferralRequest, referralRequest.getId());
          bundle.addEntry()
              .setFullUrl(url)
              .setResource(referralRequest);

          // Dereference primary concern (Conditions)
          referralRequest.getReasonReference()
              .forEach(reference -> addResource(bundle, reference, referralRequest.getIdElement()));

          // Dereference secondary concerns (Conditions)
          referralRequest.getSupportingInfo()
              .forEach(reference -> addResource(bundle, reference, referralRequest.getIdElement()));

          addAppointment(url, bundle);
        });
  }

  private void addAppointment(String referralRequest, Bundle bundle) {
    appointmentService.getByReferral(referralRequest)
        .ifPresent(app -> {
          String id = referenceService.buildId(app.getIdElement());
          bundle.addEntry()
              .setFullUrl(id)
              .setResource(app);
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

  public void addCarePlans(Bundle bundle, Long caseId) {
    List<CaseCarePlan> carePlans = encounterService.getCaseCarePlan(caseId);
    for (CaseCarePlan carePlan : carePlans) {
      Reference reference = new Reference(carePlan.getReference());
      Preconditions.checkArgument(reference.getReferenceElement().isAbsolute(),
          "CarePlan must have absolute reference");
      addResource(bundle, reference, null);
    }
  }

  private void addList(Bundle bundle, Long caseId) {
    bundle.addEntry()
        .setResource(listService.buildFromCase(caseId))
        .setFullUrl(referenceService.buildId(ResourceType.List, caseId)); //Use EncounterID as ListID

  }

  private void addCompositions(Bundle bundle, Long caseId) {
    List<Composition> compositions = compositionService.getAllByEncounter(caseId);
    compositions.stream()
        .map(comp -> new BundleEntryComponent()
            .setFullUrl(referenceService
                .buildId(ResourceType.Composition, comp.getIdElement().getIdPartAsLong()))
            .setResource(comp))
        .forEach(bundle::addEntry);
  }

  @Read
  public Encounter getEncounter(@IdParam IdType id) {
    return encounterService.getEncounter(id.getIdPartAsLong());
  }

  @Search
  public List<Encounter> searchByPatient(
      @RequiredParam(name = Encounter.SP_PATIENT, chainWhitelist = Patient.SP_IDENTIFIER) ReferenceParam param) {

    TokenParam identifier = param.toTokenParam(context);

    return encounterService.getByPatientIdentifier(identifier.getSystem(), identifier.getValue());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Encounter.class;
  }
}
