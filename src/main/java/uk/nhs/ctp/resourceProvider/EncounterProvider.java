package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.List;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.EncounterService;
import uk.nhs.ctp.service.ReferenceService;
import uk.nhs.ctp.service.StorageService;

@Component
@AllArgsConstructor
public class EncounterProvider implements IResourceProvider {

  private StorageService storageService;
  private EncounterService encounterService;
  private ReferenceService referenceService;


  @Operation(name = "$UEC-Report", idempotent = true, type = Encounter.class)
  public Bundle getEncounterReport(@IdParam IdType encounterIdType) {

    Bundle bundle = new Bundle();
    bundle.setType(BundleType.DOCUMENT);
    Long encounterIdLong = encounterIdType.getIdPartAsLong();
    Encounter encounter = encounterService.getEncounter(encounterIdLong);
    String encounterRefString = referenceService.buildId(ResourceType.Encounter, encounterIdLong);
    bundle.addEntry(new BundleEntryComponent()
        .setFullUrl(encounterRefString)
        .setResource(encounter));

    Patient patient = storageService
        .findResource(encounter.getSubject().getReference(), Patient.class);
    bundle.addEntry()
        .setFullUrl(referenceService.buildId(patient.getIdElement()))
        .setResource(patient);

    encounterService.getReferralRequestForEncounter(encounterIdLong)
      .ifPresent(referralRequest -> bundle.addEntry()
          .setFullUrl(
              referenceService.buildId(ResourceType.ReferralRequest, referralRequest.getId()))
          .setResource(referralRequest));

    List<Observation> observations = encounterService.getObservationsForEncounter(encounterIdLong);
    observations.stream()
        .map(obs -> new BundleEntryComponent()
              .setFullUrl(referenceService.buildId(ResourceType.Observation, obs.getIdElement().getIdPartAsLong()))
              .setResource(obs))
        .forEach(bundle::addEntry);

    return bundle;
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
