package uk.nhs.ctp.transform;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.ContactPoint;

@Component
public class ContactPointTransformer
    implements Transformer<ContactPoint, org.hl7.fhir.dstu3.model.ContactPoint> {

  @Override
  public org.hl7.fhir.dstu3.model.ContactPoint transform(ContactPoint from) {
    if (from == null) {
      return null;
    }

    return new org.hl7.fhir.dstu3.model.ContactPoint()
        .setValue(from.getValue())
        .setSystem(ContactPointSystem.fromCode(from.getSystem().toCode()))
        .setUse(ContactPointUse.fromCode(from.getUse().toCode()));
  }

  public List<org.hl7.fhir.dstu3.model.ContactPoint> transform(List<ContactPoint> from) {
    if (from == null) {
      return null;
    }

    return IntStream.rangeClosed(1, from.size())
        .mapToObj(i -> Pair.of(i, transform(from.get(i - 1))))
        .peek(p -> p.getRight().setRank(p.getLeft()))
        .map(Pair::getRight)
        .collect(Collectors.toList());
  }
}
