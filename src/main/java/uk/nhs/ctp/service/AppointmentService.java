package uk.nhs.ctp.service;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.IdType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.transform.AppointmentTransformer;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentTransformer appointmentTransformer;
  private final Registry<uk.nhs.ctp.model.Appointment> appointmentRegistry;

  public List<Appointment> getAll() {
    return appointmentRegistry.getAll()
        .stream()
        .map(appointmentTransformer::transform)
        .collect(Collectors.toList());
  }

  public Appointment get(IdType id) {
    return appointmentRegistry.getAll()
        .stream()
        .filter(p -> p.getId().equals(id.getIdPart()))
        .findFirst()
        .map(appointmentTransformer::transform)
        .orElseThrow(() -> new ResourceNotFoundException(id));
  }
}
