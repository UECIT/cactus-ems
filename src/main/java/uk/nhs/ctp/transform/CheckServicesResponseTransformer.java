package uk.nhs.ctp.transform;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;
import uk.nhs.ctp.transform.bundle.CheckServicesResponseBundle;

@Component
@RequiredArgsConstructor
public class CheckServicesResponseTransformer
    implements Transformer<CheckServicesResponseBundle, Stream<HealthcareServiceDTO>> {

  private final HealthcareServiceInTransformer healthcareServiceInTransformer;

  @Override
  public Stream<HealthcareServiceDTO> transform(CheckServicesResponseBundle bundle) {
    if (bundle == null) {
      return Stream.empty();
    }

    var from = bundle.getResponseParameters();

    return from.getParameter()
        .stream()
        .filter(p -> "return".equals(p.getName()))
        .findFirst()
        .map(ParametersParameterComponent::getResource)
        .map(Bundle.class::cast)
        .orElseThrow()
        .getEntry()
        .stream()
        .map(entry -> {
          HealthcareService resource = (HealthcareService) entry.getResource();

          // Establish full URL of resource
          if (entry.hasFullUrl()) {
            resource.setId(entry.getFullUrl());
          } else if (resource.hasId() && !resource.getIdElement().isAbsolute()) {
            IdType fullId = resource.getIdElement()
                .withServerBase(bundle.getBaseUrl(), resource.getResourceType().name());
            resource.setId(fullId);
          }

          return resource;
        })
        .map(healthcareServiceInTransformer::transform);
  }
}
