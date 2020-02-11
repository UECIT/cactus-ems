package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.AppointmentService;

@Component
@AllArgsConstructor
public class AppointmentProvider implements IResourceProvider {

  private final AppointmentService appointmentService;

  @Read
  public Appointment getAppointment(@IdParam IdType id) {
    return appointmentService.get(id);
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Appointment.class;
  }
}
