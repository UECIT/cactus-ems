package uk.nhs.ctp.service.search;

import static uk.nhs.ctp.utils.DateUtils.ageCodeFromDate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementDateFilterComponent;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRDefinedType;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.enums.Setting;
import uk.nhs.ctp.enums.UserType;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;

@Component
@AllArgsConstructor
public class SearchParametersTransformer {

  private GenericResourceLocator resourceLocator;

  private static final String OBSERVATION_TYPE = "Observation";

  public SearchParameters transform(List<DataRequirement> dataRequirements,
      SettingsDTO settingsDTO,
      String patientId) {

    var builder = SearchParameters.builder()
        .query("triage")
        .status("active")
        .experimental("false")
        .patientTriggers(transformPatientTriggers(dataRequirements))
        .observationTriggers(transformObservationTriggers(dataRequirements));

    if (settingsDTO != null) {
      Setting setting = Setting.fromCode(settingsDTO.getSetting().getCode());
      UserType initiatingType = setting == Setting.ONLINE
          ? UserType.fromCode(settingsDTO.getUserType().getCode())
          : UserType.PRACTITIONER;
      builder.contextValue("user", SystemURL.CS_PROVIDER_TAXONOMY, initiatingType.getValue())
          .contextValue("setting", setting.getSystem(), setting.getValue())
          .contextValue("task", SystemURL.CS_CDS_STUB, settingsDTO.getUserTaskContext().getCode())
          .jurisdiction(settingsDTO.getJurisdiction().getCode());
    }

    if (patientId != null) {
      Patient patient = resourceLocator.findResource(patientId);

      if (patient.hasGender()) {
        builder.contextValue("gender", SystemURL.CS_GENDER, patient.getGender().toCode());
      }

      if (patient.hasBirthDate()) {
        builder.contextValue("age", SystemURL.SNOMED, ageCodeFromDate(patient.getBirthDate()));
      }
    }

    return builder.build();
  }

  private List<ObservationTrigger> transformObservationTriggers(List<DataRequirement> dataRequirements) {

    return dataRequirements.stream()
      .filter(data -> data.getType().equals(OBSERVATION_TYPE))
      .map(dataRequirement -> {
        List<DataRequirementCodeFilterComponent> codeFilter = dataRequirement.getCodeFilter();
        List<DataRequirementDateFilterComponent> dateFilter = dataRequirement.getDateFilter();

        return ObservationTrigger.builder()
            .code(getCodeFilterString(codeFilter, "code"))
            .value(getCodeFilterString(codeFilter, "value"))
            .effective(getDateFilterPath(dateFilter, "effective")
              .atZone(ZoneId.systemDefault())
              .toLocalDateTime()
              .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
      })
      .collect(Collectors.toList());
  }

  private List<PatientTrigger> transformPatientTriggers(List<DataRequirement> dataRequirements) {
    return dataRequirements.stream()
        .filter(data -> data.getType().equals(FHIRAllTypes.PATIENT.toCode()))
        .map(dataRequirement -> {
          List<DataRequirementDateFilterComponent> dateFilter = dataRequirement.getDateFilter();

          return PatientTrigger.builder()
              .birthDate(getDateFilterPath(dateFilter, "birthDate")
                  .atZone(ZoneId.systemDefault())
                  .toLocalDate()
                  .format(DateTimeFormatter.ISO_LOCAL_DATE))
              .build();
        })
        .collect(Collectors.toList());
  }

  private String getCodeFilterString(
      List<DataRequirementCodeFilterComponent> codeFilterComponents, String path) {
    Coding coding = codeFilterComponents
        .stream()
        .filter(comp -> comp.getPath().equals(path))
        .findFirst()
        .orElseThrow(IllegalStateException::new)
        .getValueCodingFirstRep();
    return coding.getSystem() + "|" + coding.getCode();
  }

  private Instant getDateFilterPath(
      List<DataRequirementDateFilterComponent> dateFilterComponents, String path) {
    return dateFilterComponents.stream()
        .filter(comp -> comp.getPath().equals(path))
        .findFirst()
        .orElseThrow(IllegalStateException::new)
        .getValueDateTimeType()
        .getValue()
        .toInstant();
  }
}
