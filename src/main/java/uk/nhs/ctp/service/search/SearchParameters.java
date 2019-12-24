package uk.nhs.ctp.service.search;

import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
@Getter
public class SearchParameters {

  private static final String SP_QUERY = "_query";
  private static final String SP_EXPERIMENTAL = "experimental";
  private static final String SP_STATUS = "status";
  private static final String SP_OBSERVATION_TRIGGER = "trigger-type-code-value-effective";
  private static final String SP_PATIENT_TRIGGER = "trigger-type-date";
  private static final String SP_CONTEXT_VALUE = "useContext-code-value";
  private static final String SP_JURISDICTION = "jurisdiction";
  private static final String SP_EFFECTIVE_PERIOD_START = "effectivePeriod.start";
  private static final String SP_EFFECTIVE_PERIOD_END = "effectivePeriod.end";

  private String query;
  private String jurisdiction;
  private List<String> patientTriggers;
  private List<String> observationTriggers;
  private List<String> contextValueCode;
  private List<String> contextQuantityCode;
  private String status;
  private String experimental;
  private String effectivePeriodStart;
  private String effectivePeriodEnd;

  public MultiValueMap<String, String> toMultiValueMap() {
    LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(SP_QUERY, query);
    if (StringUtils.isNotEmpty(status)) {
      map.add(SP_STATUS, status);
    }
    if (StringUtils.isNotEmpty(experimental)) {
      map.add(SP_EXPERIMENTAL, experimental);
    }
    if (StringUtils.isNotEmpty(jurisdiction)) {
      map.add(SP_JURISDICTION, jurisdiction);
    }
    if (StringUtils.isNotEmpty(effectivePeriodStart)) {
      map.add(SP_EFFECTIVE_PERIOD_START, effectivePeriodStart);
    }
    if (StringUtils.isNotEmpty(effectivePeriodEnd)) {
      map.add(SP_EFFECTIVE_PERIOD_END, effectivePeriodEnd);
    }
    if (CollectionUtils.isNotEmpty(observationTriggers)) {
      map.put(SP_OBSERVATION_TRIGGER, observationTriggers);
    }
    if (CollectionUtils.isNotEmpty(patientTriggers)) {
      map.put(SP_PATIENT_TRIGGER, patientTriggers);
    }
    if (CollectionUtils.isNotEmpty(contextValueCode)) {
      map.put(SP_CONTEXT_VALUE, contextValueCode);
    }

    return map;
  }

  public static class SearchParametersBuilder {
    public SearchParametersBuilder contextValue(String context, String system, String code) {
      if (this.contextValueCode == null) {
        this.contextValueCode = new ArrayList<>();
      }

      this.contextValueCode.add(context + "$" + system + "|" + code);
      return this;
    }

    public SearchParametersBuilder effectivePeriodStart(ParamPrefixEnum mod, LocalDate date) {
      this.effectivePeriodStart = mod.getValue()
          + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
      return this;
    }

    public SearchParametersBuilder effectivePeriodEnd(ParamPrefixEnum mod, LocalDate date) {
      this.effectivePeriodEnd = mod.getValue()
          + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
      return this;
    }

    public SearchParametersBuilder observationTriggers(List<ObservationTrigger> observationTriggers) {
      if (this.observationTriggers == null) {
        this.observationTriggers = new ArrayList<>();
      }

      observationTriggers.forEach(trigger -> {
        StringJoiner joiner = new StringJoiner("$")
            .add("CareConnectObservation")
            .add("code")
            .add(trigger.getCode())
            .add("value")
            .add(trigger.getValue())
            .add("effective")
            .add(trigger.getEffective());

        this.observationTriggers.add(joiner.toString());
      });

      return this;
    }

    public SearchParametersBuilder patientTriggers(List<PatientTrigger> patientTriggers) {
      if (this.patientTriggers == null) {
        this.patientTriggers = new ArrayList<>();
      }

      patientTriggers.forEach(trigger -> {
        StringJoiner joiner = new StringJoiner("$")
            .add("CareConnectPatient")
            .add("birthDate")
            .add(trigger.getBirthDate());

        this.patientTriggers.add(joiner.toString());
      });

      return this;
    }

  }

}
