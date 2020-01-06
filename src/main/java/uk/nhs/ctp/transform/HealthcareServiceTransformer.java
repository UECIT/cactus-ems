package uk.nhs.ctp.transform;

import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Endpoint;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.HealthcareService;

@Component
public class HealthcareServiceTransformer implements Transformer<org.hl7.fhir.dstu3.model.HealthcareService, HealthcareService> {

  @Override
  public HealthcareService transform(org.hl7.fhir.dstu3.model.HealthcareService healthcareService) {
    return HealthcareService.builder()
        .active(healthcareService.getActive())
        .addresses(healthcareService.getContained().stream()
            .map(ep -> ((Endpoint)ep).getAddress())
            .collect(Collectors.toList()))
        .appointmentRequired(healthcareService.getAppointmentRequired())
        .name(healthcareService.getName())
        .build();
  }
}
