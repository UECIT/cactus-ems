package uk.nhs.ctp.transform;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.CareConnectIdentifier;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.Identifier;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Component
@RequiredArgsConstructor
public class IdentifierTransformer
    implements Transformer<Identifier, CareConnectIdentifier> {

  private final ReferenceService referenceService;

  @Override
  public CareConnectIdentifier transform(Identifier from) {
    if (from == null || isEmpty(from.getValue())) {
      return null;
    }

    var identifier = new CareConnectIdentifier();

    identifier.setUse(IdentifierUse.OFFICIAL);
    identifier.setSystem(from.getSystem());
    identifier.setValue(from.getValue());

    if (from.getIssuerId() != null) {
      identifier.setAssigner(
          referenceService.buildRef(ResourceType.Organization, from.getIssuerId()));
    }

    if (from.getType() != null) {
      identifier.setType(from.getType().toCodeableConcept());
    }

    return identifier;
  }
  
  public List<CareConnectIdentifier> transform(Collection<Identifier> from) {
    return from.stream().map(this::transform).collect(Collectors.toList());
  }
}
