package uk.nhs.ctp.utils;

import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtils {

  public static Predicate<Reference> referenceTo(ResourceType resourceType) {
    return reference -> {
      if (reference.hasReferenceElement()) {
        return reference.getReferenceElement().getResourceType().equals(resourceType.toString());
      }
      else if (reference.getResource() != null) {
        return reference.getResource().fhirType().equals(resourceType.toString());
      }
      else {
        return false;
      }
    };
  }

}
