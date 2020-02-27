package uk.nhs.ctp.service.fhir;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.CdssService;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.search.SearchParametersTransformer;

@Component
@AllArgsConstructor
public class SwitchTriggerResolver {

  private final CdssService cdssService;
  private final SearchParametersTransformer searchParametersTransformer;

  public String getSwitchTrigger(GuidanceResponse guidanceResponse, SettingsDTO settingsDTO, String patientId) {

    var dataRequirements = guidanceResponse.getDataRequirement();

    if (!dataRequirements.isEmpty()) {
      var searchParams = searchParametersTransformer.transform(dataRequirements, settingsDTO, patientId);
      var serviceDefBySupplier = cdssService.queryServiceDefinitions(searchParams);
      var matchedService = serviceDefBySupplier.stream()
          .findFirst()
          .flatMap(supplier -> supplier.getServiceDefinitions().stream()
              .findFirst()
              .map(sd -> supplier.getId() + "/" + sd.getServiceDefinitionId())
          );

      return matchedService.orElse(null);
    }

    return null;
  }

}
