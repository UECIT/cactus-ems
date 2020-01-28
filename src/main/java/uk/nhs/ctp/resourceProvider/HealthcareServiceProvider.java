package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.HealthcareServiceService;

@AllArgsConstructor
@Component
public class HealthcareServiceProvider {

  private HealthcareServiceService healthcareServiceService;

  @Operation(name = "$check-services", type = HealthcareService.class)
  public Bundle searchForHealthcareServices(
      @OperationParam(name = "referralRequest") ReferralRequest referralRequest) {

    Bundle bundle = new Bundle();
    healthcareServiceService.getAll().stream()
      .map(service -> new BundleEntryComponent().setResource(service))
      .forEach(bundle::addEntry);
    return bundle;
  }
}
