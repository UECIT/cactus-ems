package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.builder.RelatedPersonBuilder;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Component
@AllArgsConstructor
public class RelatedPersonProvider implements IResourceProvider {

  private final RelatedPersonBuilder relatedPersonBuilder;
  private final ReferenceService referenceService;

  @Read //RelatedPerson.id = Patient.id
  public RelatedPerson get(@IdParam IdType id) {
    Reference patientRef = referenceService.buildRef(ResourceType.Patient, id.getIdPartAsLong());
    return relatedPersonBuilder.build(patientRef);
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return RelatedPerson.class;
  }
}
