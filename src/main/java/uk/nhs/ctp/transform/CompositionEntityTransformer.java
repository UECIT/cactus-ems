package uk.nhs.ctp.transform;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.CompositionEntity;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Service
@AllArgsConstructor
public class CompositionEntityTransformer implements
    Transformer<CompositionEntity, Composition> {

  private final FhirContext fhirContext;
  private final ReferenceService referenceService;

  @Override
  public Composition transform(CompositionEntity entity) {
    IParser fhirParser = fhirContext.newJsonParser();
    Composition composition = fhirParser.parseResource(Composition.class, entity.getResource());

    if (entity.getId() != null) {
      composition.setId(referenceService.buildId(ResourceType.Composition, entity.getId()));
    }

    return composition;
  }
}
