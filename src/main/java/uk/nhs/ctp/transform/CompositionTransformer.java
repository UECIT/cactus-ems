package uk.nhs.ctp.transform;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Composition;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CompositionEntity;

@Service
@AllArgsConstructor
public class CompositionTransformer implements
    Transformer<Composition, CompositionEntity> {

  FhirContext fhirContext;

  @Override
  public CompositionEntity transform(Composition composition) {
    IParser parser = fhirContext.newJsonParser();
    CompositionEntity compositionEntity = new CompositionEntity();
    compositionEntity.setResource(parser.encodeResourceToString(composition));
    return compositionEntity;
  }
}
