package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.AppointmentService;

@Component
@RequiredArgsConstructor
public class AppointmentProvider implements IResourceProvider {

  private final AppointmentService appointmentService;

  @Search
  public Collection<Appointment> findByIncomingReferral(
      @RequiredParam(name = Appointment.SP_INCOMINGREFERRAL) ReferenceParam incomingReferralRef
  ) {
    String referral = incomingReferralRef.getValue();
    return appointmentService.getByReferral(referral).stream()
        .collect(Collectors.toList());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Appointment.class;
  }
}
