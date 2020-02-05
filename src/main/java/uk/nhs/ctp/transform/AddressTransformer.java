package uk.nhs.ctp.transform;

import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.Address;

@Component
public class AddressTransformer implements Transformer<Address, org.hl7.fhir.dstu3.model.Address> {

  @Override
  public org.hl7.fhir.dstu3.model.Address transform(Address from) {
    if (from == null) {
      return null;
    }

    return new org.hl7.fhir.dstu3.model.Address()
        .setLine(from.getLines()
            .stream()
            .map(StringType::new)
            .collect(Collectors.toList()))
        .setCity(from.getCity())
        .setPostalCode(from.getPostcode())
        .setType(AddressType.PHYSICAL)
        .setUse(AddressUse.fromCode(from.getUse().toCode()));
  }
}
