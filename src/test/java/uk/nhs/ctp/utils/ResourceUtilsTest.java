package uk.nhs.ctp.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.function.Predicate;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.Test;

public class ResourceUtilsTest {

  @Test
  public void referenceTo_withMatchingReferenceElement() {
    Predicate<Reference> encounterPredicate = ResourceUtils.referenceTo(ResourceType.Encounter);

    Reference encounterRef = new Reference();
    encounterRef.setReference("Encounter/someid");
    boolean result = encounterPredicate.test(encounterRef);

    assertThat(result, is(true));
  }

  @Test
  public void referenceTo_withNotMatchingReferenceElement() {
    Predicate<Reference> encounterPredicate = ResourceUtils.referenceTo(ResourceType.Encounter);

    Reference encounterRef = new Reference();
    encounterRef.setReference("Composition/someid");
    boolean result = encounterPredicate.test(encounterRef);

    assertThat(result, is(false));
  }

  @Test
  public void referenceTo_withMatchingResourceElement() {
    Predicate<Reference> patientPredicate = ResourceUtils.referenceTo(ResourceType.Patient);

    Patient patient = new Patient();
    patient.setId("Some id");
    boolean result = patientPredicate.test(new Reference(patient));

    assertThat(result, is(true));
  }

  @Test
  public void referenceTo_withNotMatchingResourceElement() {
    Predicate<Reference> patientPredicate = ResourceUtils.referenceTo(ResourceType.Patient);

    Condition condition = new Condition();
    condition.setId("Some id");
    boolean result = patientPredicate.test(new Reference(condition));

    assertThat(result, is(false));
  }

  @Test
  public void referenceTo_withNoReferenceOrResource() {
    Predicate<Reference> rpPredicate = ResourceUtils.referenceTo(ResourceType.RelatedPerson);

    boolean result = rpPredicate.test(new Reference());

    assertThat(result, is(false));
  }

}