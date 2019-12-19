package uk.nhs.ctp.service.search;

import static uk.nhs.ctp.utils.DateUtils.ageCodeFromDate;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.service.PatientService;
import uk.nhs.ctp.service.dto.SettingsDTO;

@Component
@AllArgsConstructor
public class SearchParametersTransformer {

  private PatientService patientService;

  public SearchParameters transform(List<String> triggers, SettingsDTO settingsDTO, Long patientId) {

    var builder = SearchParameters.builder()
        .query("triage")
        .typeCode(triggers);

    if (settingsDTO != null) {
      builder.contextValue("user", settingsDTO.getUserType().getCode())
          .contextValue("setting", settingsDTO.getSetting().getCode())
          .contextValue("task", settingsDTO.getUserTaskContext().getCode())
          .jurisdiction(settingsDTO.getJurisdiction().getCode());
    }

    if (patientId != null) {
      PatientEntity patient = patientService.findById(patientId);

      builder.contextValue("gender", patient.getGender())
          .contextValue("age", ageCodeFromDate(patient.getDateOfBirth()));
    }

    return builder.build();
  }

}
