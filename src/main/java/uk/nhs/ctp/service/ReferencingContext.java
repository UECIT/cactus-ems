package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.dstu3.model.Resource;
import uk.nhs.ctp.enums.ReferencingType;

public class ReferencingContext {

  private ReferencingType referencingType;
  private List<Resource> referencedResources = new ArrayList<>();

  public ReferencingContext(ReferencingType referencingType) {
    this.referencingType = referencingType;
  }

  public List<Resource> getReferencedResources() {
    return referencedResources;
  }

  public boolean shouldUpload() {
    return referencingType == ReferencingType.ServerReferences
        || referencingType == ReferencingType.BundledReferences;
  }

  public boolean shouldBundle() {
    return referencingType == ReferencingType.BundledReferences;
  }
}
