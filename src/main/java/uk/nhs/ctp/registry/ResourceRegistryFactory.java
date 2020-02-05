package uk.nhs.ctp.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;

@Component
@RequiredArgsConstructor
public class ResourceRegistryFactory {

  @Qualifier("enhanced")
  private final ObjectMapper mapper;
  private final HealthcareServiceRegistry healthcareServiceRegistry;

  @SuppressWarnings("unchecked")
  public <T> Registry<T> getRegistry(Class<T> type) {
    if (HealthcareServiceDTO.class.equals(type)) {
      return (Registry<T>) healthcareServiceRegistry;
    }

    return new ResourceRegistry<>(mapper, type);
  }
}
