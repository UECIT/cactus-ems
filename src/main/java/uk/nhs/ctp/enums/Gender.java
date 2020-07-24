package uk.nhs.ctp.enums;

import com.google.common.base.CaseFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements Concept {
  FEMALE,
  MALE,
  OTHER,
  UNKNOWN;

  private final String system = "http://hl7.org/fhir/administrative-gender";
  private final String value = name().toLowerCase();
  private final String display = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());

  public static Gender fromCode(String code) {
    return Arrays.stream(Gender.values())
        .filter(type -> type.value.equals(code))
        .findFirst().orElseThrow(IllegalArgumentException::new);
  }
}
