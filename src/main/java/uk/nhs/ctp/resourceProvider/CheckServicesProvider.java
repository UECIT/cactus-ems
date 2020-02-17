package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.param.NumberParam;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.HealthcareServiceService;


@AllArgsConstructor
@Component
public class CheckServicesProvider {

  private HealthcareServiceService healthcareServiceService;

  @Operation(name = "$check-services")
  public Parameters searchForHealthcareServices(
      @OperationParam(name = "referralRequest", min = 1, max = 1) ReferralRequest referralRequest,
      @OperationParam(name = "patient", min = 1, max = 1) Patient patient,
      @OperationParam(name = "requestId", max = 1) IdType requestId,
      @OperationParam(name = "location", max = 1) Location location,
      @OperationParam(name = "requester", max = 1) IBaseResource requester,
      @OperationParam(name = "searchDistance", max = 1) NumberParam searchDistance,
      @OperationParam(name = "registeredGP", max = 1) Organization registeredGp,
      @OperationParam(name = "inputParameters", max = 1) Parameters inputParameters
  ) {

    Bundle returnedServices = new Bundle();
    healthcareServiceService.getAll(referralRequest.getContext()).stream()
        .map(service -> new BundleEntryComponent().setResource(service))
        .forEach(returnedServices::addEntry);

    var outputParameters = new Parameters();

    var wrappingParameters = new Parameters();
    wrappingParameters.addParameter().setName("return").setResource(returnedServices);
    wrappingParameters.addParameter().setName("outputParameters").setResource(outputParameters);

    return wrappingParameters;
  }

}
