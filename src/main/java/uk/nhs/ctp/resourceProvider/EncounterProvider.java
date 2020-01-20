package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntrySearchComponent;
import org.hl7.fhir.dstu3.model.Bundle.SearchEntryMode;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.encounterreport.EncounterReportService;

@Component
@AllArgsConstructor
public class EncounterProvider implements IResourceProvider {

  private static final String REFERRAL_REQUEST_CONTEXT_INCLUDES = "ReferralRequest:context";
  private static final String COMPOSITION_ENCOUNTER_INCLUDES = "Composition:encounter";
  private static final String ENCOUNTER_SUBJECT_INCLUDES = "Encounter:subject";

  private EncounterReportService encounterReportService;

  @Search()
  public Bundle getEncounterReport(
      @RequiredParam(name = Encounter.SP_RES_ID) StringParam id,
      @IncludeParam(reverse = true, allow = {REFERRAL_REQUEST_CONTEXT_INCLUDES, COMPOSITION_ENCOUNTER_INCLUDES}) Set<Include> revIncludes,
      @IncludeParam(allow = {ENCOUNTER_SUBJECT_INCLUDES}) Set<Include> include
  ) {
    String encounterId = id.getValue();
    Bundle bundle = new Bundle();

    Encounter encounter = encounterReportService.getEncounter(encounterId);
    bundle.addEntry(new BundleEntryComponent().setResource(encounter));

    if (include.contains(new Include(ENCOUNTER_SUBJECT_INCLUDES))) {
      Patient patient = encounter.hasSubject()
          ? encounterReportService.getPatient(encounter.getSubject().getId())
          : null;
      if (patient != null) {
        bundle.addEntry()
            .setResource(patient)
            .setSearch(new BundleEntrySearchComponent()
              .setMode(SearchEntryMode.INCLUDE));
      }
    }

    if (revIncludes.contains(new Include(REFERRAL_REQUEST_CONTEXT_INCLUDES))) {
      List<ReferralRequest> referralRequests = encounterReportService.getReferralRequests(encounterId);
      referralRequests.stream()
          .map(rr -> new BundleEntryComponent()
              .setResource(rr)
              .setSearch(new BundleEntrySearchComponent()
                  .setMode(SearchEntryMode.INCLUDE)))
          .forEach(bundle::addEntry);
    }

    if (revIncludes.contains(new Include(COMPOSITION_ENCOUNTER_INCLUDES))) {
      List<Composition> compositions = encounterReportService.getCompositions(encounterId);
      compositions.stream()
          .map(comp -> new BundleEntryComponent()
              .setResource(comp)
              .setSearch(new BundleEntrySearchComponent()
                  .setMode(SearchEntryMode.INCLUDE)))
          .forEach(bundle::addEntry);
    }
    return bundle;
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Encounter.class;
  }
}
