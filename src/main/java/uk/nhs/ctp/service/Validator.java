package uk.nhs.ctp.service;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class Validator<T> {

  private T value;
  private String name;

  public Validator(T value, String name) {
    this.value = value;
    this.name = name;
  }

  public Validator<Object> checkSingle() {
    Preconditions.checkState(value instanceof Collection, "Expected collection for %s", name);

    int count = ((Collection<?>) value).size();
    Preconditions.checkState(count == 1,
        "Found %d elements for %s but expecting single", count, name);
    return new Validator<>(((Collection<?>) value).iterator().next(), name);
  }

  public <R> Validator<R> checkType(Class<R> type) {
    Object checkValue = value;
    if (value instanceof ParametersParameterComponent) {
      if (((ParametersParameterComponent) value).hasValue()) {
        checkValue = ((ParametersParameterComponent) value).getValue();
      } else {
        checkValue = ((ParametersParameterComponent) value).getResource();
      }
    }
    Preconditions.checkState(type.isInstance(checkValue),
        "Expected instance of %s for %s", type, name);
    return new Validator<>(type.cast(checkValue), name);
  }

  public Validator<Reference> checkReferenceType(ResourceType... validTypes) {
    Validator<Reference> ref = checkType(Reference.class);
    ResourceType refType = null;
    if (ref.value.hasReferenceElement()) {
      refType = ResourceType.fromCode(ref.value.getReferenceElement().getResourceType());
    }
    else {
      IBaseResource resource = ref.value.getResource();
      if (resource instanceof Resource) {
        refType = ResourceType.fromCode(((Resource) resource).fhirType());
      }
    }
    Preconditions.checkState(Arrays.asList(validTypes).contains(refType),
        "Expected %s to be reference to %s", name, validTypes);
    return ref;
  }

  public Validator<CodeableConcept> checkCodeableConcept(Collection<String> validCodes) {
    Validator<CodeableConcept> concept = checkType(CodeableConcept.class);
    Preconditions.checkState(validCodes.contains(concept.value.getCodingFirstRep().getCode()),
        "Expected %s to be CodeableConcept with code %s", name, validCodes);
    return concept;
  }

  public void checkAbsent() {
    Preconditions.checkState(
        value == null || (value instanceof Collection && ((Collection<?>) value).isEmpty()),
        "%s not expected to be present", name);
  }
}
