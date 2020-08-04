package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ListOrder implements Concept {
  EVENT_DATE("event-date", "Sorted by Event Date");

  private final String system = "http://hl7.org/fhir/list-order";
  private final String value;
  private final String display;
}
