package uk.nhs.ctp.service.search;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import lombok.Builder;
import lombok.Getter;
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
  private static final String SP_SEARCH_DATE_TIME = "searchDateTime";

  private String query;
  private String jurisdiction;
  private List<String> patientTriggers;
  private List<String> observationTriggers;
  private List<String> contextValueCode;
  private List<String> contextQuantityCode;
  private String status;
  private String experimental;

  public MultiValueMap<String, String> toMultiValueMap() {
    LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();

    if (isNotEmpty(query)) {
      map.add(SP_QUERY, query);
    }
    if (isNotEmpty(status)) {
      map.add(SP_STATUS, status);
    }
    if (isNotEmpty(experimental)) {
      map.add(SP_EXPERIMENTAL, experimental);
    }
    if (isNotEmpty(jurisdiction)) {
      map.add(SP_JURISDICTION, jurisdiction);
    }
    if (isNotEmpty(observationTriggers)) {
      map.put(SP_OBSERVATION_TRIGGER, observationTriggers);
    }
    if (isNotEmpty(patientTriggers)) {
      map.put(SP_PATIENT_TRIGGER, patientTriggers);
    }
    if (isNotEmpty(contextValueCode)) {
      map.put(SP_CONTEXT_VALUE, contextValueCode);
    }
    if (isNotEmpty(query)) {
      map.add(SP_SEARCH_DATE_TIME, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
