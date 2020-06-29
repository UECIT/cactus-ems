package uk.nhs.ctp.auditFinder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OperationType {

  @JsonProperty("Service Search")
  SERVICE_SEARCH("service_search"),
  @JsonProperty("Encounter")
  ENCOUNTER("encounter");

  private final String name;

}
