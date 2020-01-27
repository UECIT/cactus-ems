package uk.nhs.ctp.service.search;

import static ca.uhn.fhir.rest.param.ParamPrefixEnum.GREATERTHAN_OR_EQUALS;
import static ca.uhn.fhir.rest.param.ParamPrefixEnum.LESSTHAN_OR_EQUALS;
import static uk.nhs.ctp.SystemURL.CS_CDS_STUB;
import static uk.nhs.ctp.SystemURL.CS_GENDER;
import static uk.nhs.ctp.SystemURL.CS_PROVIDER_TAXONOMY;
import static uk.nhs.ctp.SystemURL.SNOMED;
import static uk.nhs.ctp.utils.DateUtils.ageCodeFromDate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementDateFilterComponent;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.service.dto.SettingsDTO;

@Component
@AllArgsConstructor
public class SearchParametersTransformer {

  private PatientRepository patientService;

  public SearchParameters transform(List<DataRequirement> dataRequirements,
      SettingsDTO settingsDTO,
      Long patientId) {

    var builder = SearchParameters.builder()
        .query("triage")
        .status("active")
        .experimental("false")
        .effectivePeriodStart(LESSTHAN_OR_EQUALS, LocalDate.now())
        .effectivePeriodEnd(GREATERTHAN_OR_EQUALS, LocalDate.now())
        .patientTriggers(transformPatientTriggers(dataRequirements))
        .observationTriggers(transformObservationTriggers(dataRequirements));

    if (settingsDTO != null) {
      builder.contextValue("user", CS_PROVIDER_TAXONOMY, settingsDTO.getUserType().getCode())
          .contextValue("setting", CS_PROVIDER_TAXONOMY, settingsDTO.getSetting().getCode())
          .contextValue("task", CS_CDS_STUB, settingsDTO.getUserTaskContext().getCode())
          .jurisdiction(settingsDTO.getJurisdiction().getCode());
    }

    if (patientId != null) {
      PatientEntity patient = patientService.findById(patientId);

      builder.contextValue("gender", CS_GENDER, patient.getGender())
          .contextValue("age", SNOMED, ageCodeFromDate(patient.getDateOfBirth()));
    }

    return builder.build();
  }

  private List<ObservationTrigger> transformObservationTriggers(List<DataRequirement> dataRequirements) {

    return dataRequirements.stream()
      .filter(data -> data.getType().equals(CareConnectObservation.class.getSimpleName()))
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
        .filter(data -> data.getType().equals(CareConnectPatient.class.getSimpleName()))
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
