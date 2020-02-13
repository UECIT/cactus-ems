package uk.nhs.ctp.enums;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserType implements Concept {
  PATIENT("Patient", "Patient"),
  RELATED_PERSON("RelatedPerson", "Related Person"),
  PRACTITIONER("Practitioner", "Practitioner");

  private final String value;
  private final String display;

  public static UserType fromCode(String code) {
    return Arrays.stream(UserType.values())
        .filter(type -> type.value.equals(code))
        .findFirst().orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public String getDisplay() {
    return display;
  }

  @Override
  public String getSystem() {
    return "https://developer.nhs.uk/apis/cds-api-1-1-0/api_post_service_definition.html#usertype-element";
  }

  @Override
  public String getValue() {
    return value;
  }
}
