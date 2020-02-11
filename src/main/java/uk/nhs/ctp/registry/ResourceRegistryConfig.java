package uk.nhs.ctp.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.ctp.model.Appointment;
import uk.nhs.ctp.model.Organisation;
import uk.nhs.ctp.model.Practitioner;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;

@Configuration
@RequiredArgsConstructor
public class ResourceRegistryConfig {

  @Qualifier("enhanced")
  private final ObjectMapper mapper;

  @Bean
  public Registry<HealthcareServiceDTO> healthcareServiceDTORegistry() {
    return new HealthcareServiceRegistry();
  }

  @Bean
  public Registry<Appointment> appointmentRegistry() {
    return new AppointmentRegistry();
  }

  @Bean
  public Registry<Practitioner> practitionerRegistry() {
    return new ResourceRegistry<>(mapper, Practitioner.class);
  }

  @Bean
  public Registry<Organisation> organisationRegistry() {
    return new ResourceRegistry<>(mapper, Organisation.class);
  }
}
