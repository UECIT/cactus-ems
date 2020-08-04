package uk.nhs.ctp.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReferencingType {
  /** Resources will be stored on a FHIR server and will passed in as a literal reference */
  @JsonProperty("resourceReferenceType.ByReference")
  BY_REFERENCE,
  /** Resources will be contained within the root resource that references them */
  @JsonProperty("resourceReferenceType.ByResource")
  BY_RESOURCE
}
