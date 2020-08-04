package uk.nhs.ctp.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.transform.AppointmentTransformer;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentTransformer appointmentTransformer;
  private final Registry<uk.nhs.ctp.model.Appointment> appointmentRegistry;
  private final StorageService storageService;

  public Optional<Appointment> getByReferral(String referralRequest) {
    return storageService.getClient()
        .search()
        .forResource(Appointment.class)
        .where(Appointment.INCOMINGREFERRAL.hasId(referralRequest))
        .returnBundle(Bundle.class)
        .execute()
        .getEntry().stream()
        .map(BundleEntryComponent::getResource)
        .map(Appointment.class::cast)
        .findFirst();
  }

  public void create(ReferralRequest referralRequest) {
    uk.nhs.ctp.model.Appointment appointment = appointmentRegistry.getAll().stream()
        .findFirst()
        .orElseThrow();
    appointment.setPatientId(referralRequest.getSubject().getReference());
    appointment.setReferral(referralRequest.getId());
    appointment.setHealthcareService(referralRequest.getRecipientFirstRep());
    storageService.storeExternal(appointmentTransformer.transform(appointment));
  }
}
