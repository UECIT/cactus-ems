package uk.nhs.ctp.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Setting implements Concept {

  ONLINE("online", "Online"),
  PHONE("telephony", "Telephony"),
  FACE_TO_FACE("face-to-face", "Face to face");

  private final String value;
  private final String display;
  private final String system = SYSTEM;

  public static Setting fromCode(String code) {
    return Arrays.stream(Setting.values())
        .filter(type -> type.value.equals(code))
        .findFirst().orElseThrow(IllegalArgumentException::new);
  }

  public static final String SYSTEM = "https://fhir.nhs.uk/STU3/CodeSystem/UEC-CommunicationChannel-1";
}
