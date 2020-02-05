package uk.nhs.ctp.transform;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.HumanName;

@Component
public class HumanNameTransformer
    implements Transformer<HumanName, org.hl7.fhir.dstu3.model.HumanName> {

  @Override
  public org.hl7.fhir.dstu3.model.HumanName transform(HumanName from) {
    var name = new org.hl7.fhir.dstu3.model.HumanName();

    name.addPrefix(from.getPrefix());
    name.addGiven(from.getGiven());
    name.setFamily(from.getFamily());
    name.setText(from.getFullName());

    return name;
  }
}
