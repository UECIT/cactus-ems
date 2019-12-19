package uk.nhs.ctp.service.resolver;

import java.util.List;
import java.util.stream.Collectors;
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

  public String getSwitchTrigger(GuidanceResponse guidanceResponse, SettingsDTO settingsDTO, Long patientId) {

    var optionalDataRequirement = guidanceResponse.getDataRequirement().stream()
        .filter(data -> data.getType().equals("CareConnectObservation")).findFirst();

    if (optionalDataRequirement.isPresent()) {
      List<String> triggerCodes = optionalDataRequirement.get().getCodeFilter().stream()
          .map(filter -> "CareConnectObservation$" + filter.getValueCodingFirstRep().getCode())
          .collect(Collectors.toList());

      var searchParameters = searchParametersTransformer.transform(triggerCodes, settingsDTO, patientId);
      var serviceDefinitionBySupplier = cdssService.queryServiceDefinitions(searchParameters);

      var matchedService = serviceDefinitionBySupplier.stream()
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
