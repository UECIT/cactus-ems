package uk.nhs.ctp.resourceProvider;

import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;
import static uk.nhs.cactus.common.audit.model.AuditProperties.OPERATION_TYPE;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.param.NumberParam;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.audit.AuditService;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.builder.ParametersBuilder;
import uk.nhs.ctp.service.HealthcareServiceService;

@Component
@RequiredArgsConstructor
public class CheckServicesProvider {

  private final HealthcareServiceService healthcareServiceService;
  private final AuditService auditService;

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

    auditService.addAuditProperty(OPERATION_TYPE, OperationType.CHECK_SERVICES.getName());
    auditService.addAuditProperty(INTERACTION_ID, requestId.getValue());

    var returnedServices = new Parameters();
    healthcareServiceService.getAll(referralRequest.getContext()).stream()
        .map(service -> new ParametersParameterComponent()
            .setName(service.getId())
            .addPart()
            .setName("service")
            .setResource(service))
        .forEach(returnedServices::addParameter);

    return new ParametersBuilder().add("services", returnedServices).build();
  }

}
