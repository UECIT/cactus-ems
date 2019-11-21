package uk.nhs.ctp.enums;

public enum ReferencingType {
  /** Resources will be stored on a FHIR server and will passed in as a literal reference */
  ServerReferences,
  /** Resources will be contained within the root resource that references them */
  ContainedReferences,
  /** Resources will be included in the bundle holding the root resource that uses the references */
  BundledReferences
}
