package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdentifierType implements Concept {
  SDS("SDS user id", "https://fhir.nhs.uk/Id/sds-user-id"),
  SDSR("SDS role", "https://fhir.nhs.uk/Id/sds-role-profile-id"),
  OC("ODS organisation code", "https://fhir.nhs.uk/Id/ods-organization-code");

  private final String value = name();
  private final String display;
  private final String system;
}
