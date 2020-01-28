package uk.nhs.ctp.registry;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;

@Component
public class HealthcareServiceRegistry {

  @Value("${ems.server}")
  private String emsServer;

  public List<HealthcareServiceDTO> getAll() {

    return List.of(HealthcareServiceDTO.builder()
        .id(1)
        .active(true)
        .endpoint(emsServer + "/fhir")
        .appointmentRequired(false)
        .name("Handover")
        .description("EMS handover service")
        .email("some@email.com")
        .phoneNumber("098765456787")
        .provision(List.of("Free"))
        .build());
  }
}
