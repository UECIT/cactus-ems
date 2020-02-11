package uk.nhs.ctp.registry;

import java.util.Collections;
import java.util.List;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;

public class HealthcareServiceRegistry implements Registry<HealthcareServiceDTO> {

  public List<HealthcareServiceDTO> getAll() {
    return Collections.singletonList(HealthcareServiceDTO.builder()
        .id("HealthcareService/1")
        .active(true)
        .appointmentRequired(false)
        .name("Handover")
        .description("EMS handover service")
        .email("some@email.com")
        .phoneNumber("098765456787")
        .provision(Collections.singletonList("Free"))
        .build());
  }
}
