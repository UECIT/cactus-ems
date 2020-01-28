package uk.nhs.ctp.transform;

import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.Endpoint;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;

@Component
public class HealthcareServiceOutTransformer implements
    Transformer<HealthcareServiceDTO, HealthcareService> {

  @Override
  public HealthcareService transform(HealthcareServiceDTO dto) {
    var healthcareService = new HealthcareService();
    healthcareService.setId(Long.toString(dto.getId()));
    healthcareService.setActive(dto.isActive());
    healthcareService.addEndpoint()
        .setResource(new Endpoint().setAddress(dto.getEndpoint()));
    healthcareService.setAppointmentRequired(dto.isAppointmentRequired());
    healthcareService.setName(dto.getName());
    healthcareService.setExtraDetails(dto.getDescription());
    healthcareService.addTelecom()
        .setSystem(ContactPointSystem.EMAIL)
        .setValue(dto.getEmail());
    healthcareService.addTelecom()
        .setSystem(ContactPointSystem.PHONE)
        .setValue(dto.getPhoneNumber());
    healthcareService.addServiceProvisionCode().setCoding(
          dto.getProvision()
              .stream()
              .map(p -> new Coding().setDisplay(p))
              .collect(Collectors.toList()));

    return healthcareService;
  }
}
