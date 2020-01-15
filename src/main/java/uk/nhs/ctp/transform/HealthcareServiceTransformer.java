package uk.nhs.ctp.transform;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.Endpoint;
import org.hl7.fhir.dstu3.model.HealthcareService.HealthcareServiceAvailableTimeComponent;
import org.hl7.fhir.dstu3.model.HealthcareService.HealthcareServiceNotAvailableComponent;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.HealthcareService;

@Component
public class HealthcareServiceTransformer implements Transformer<org.hl7.fhir.dstu3.model.HealthcareService, HealthcareService> {

  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

  @Override
  public HealthcareService transform(org.hl7.fhir.dstu3.model.HealthcareService healthcareService) {

    return HealthcareService.builder()
        .active(healthcareService.getActive())
        .endpoint(((Endpoint)healthcareService.getEndpointFirstRep().getResource()).getAddress())
        .appointmentRequired(healthcareService.getAppointmentRequired())
        .name(healthcareService.getName())
        .description(healthcareService.getComment() + " " + healthcareService.getExtraDetails())
        .email(getContactPoint(healthcareService.getTelecom(), ContactPointSystem.EMAIL))
        .phoneNumber(getContactPoint(healthcareService.getTelecom(), ContactPointSystem.PHONE))
        .provision(getCodeDisplay(healthcareService.getServiceProvisionCode()))
        .availableTimes(getAvailableTimes(healthcareService.getAvailableTime()))
        .notAvailableTimes(getNotAvailableTimes(healthcareService.getNotAvailable()))
        .build();
  }

  private List<String> getNotAvailableTimes(List<HealthcareServiceNotAvailableComponent> notAvailable) {
    return notAvailable.stream()
        .map(na ->
            TIME_FORMAT.format(na.getDuring().getStart())
            + " - "
            + TIME_FORMAT.format(na.getDuring().getEnd())
            + ": "
            + na.getDescription())
        .collect(Collectors.toList());
  }

  private String getContactPoint(List<ContactPoint> contactPoints, ContactPointSystem contactPointSystem) {
    return contactPoints.stream()
        .filter(contactPoint -> contactPoint.getSystem().equals(contactPointSystem))
        .findFirst()
        .map(ContactPoint::getValue)
        .orElse(null);

  }

  private List<String> getCodeDisplay(List<CodeableConcept> codeableConcept) {
    return codeableConcept.stream()
        .map(c -> c.getCodingFirstRep().getDisplay())
        .collect(Collectors.toList());
  }

  private List<String> getAvailableTimes(List<HealthcareServiceAvailableTimeComponent> availableTime) {
    return availableTime.stream()
        .map(healthcareServiceAvailableTimeComponent -> {
          String days = healthcareServiceAvailableTimeComponent.getDaysOfWeek().stream()
              .map(day -> day.getValue().getDisplay())
              .collect(Collectors.joining(", "));

          String times = new StringJoiner("-")
              .add(healthcareServiceAvailableTimeComponent.getAvailableStartTime())
              .add(healthcareServiceAvailableTimeComponent.getAvailableEndTime())
              .toString();
          return days + ": " + times;
        })
        .collect(Collectors.toList());
  }
}
