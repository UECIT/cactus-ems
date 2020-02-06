package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.repos.CaseDataRepository;
import uk.nhs.ctp.transform.ObservationTransformer;

@Component
@AllArgsConstructor
public class ObservationProvider implements IResourceProvider {

  private final CaseDataRepository caseDataRepository;
  private final ObservationTransformer observationTransformer;

  @Read
  public Observation getObservation(@IdParam IdType id) {
    CaseObservation obs = caseDataRepository.getOne(id.getIdPartAsLong());
    return observationTransformer.transform(obs);
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Observation.class;
  }
}
