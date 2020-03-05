package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.repos.ObservationRepository;
import uk.nhs.ctp.transform.ObservationTransformer;

@Component
@AllArgsConstructor
public class ObservationProvider implements IResourceProvider {

  private final ObservationRepository observationRepository;
  private final ObservationTransformer observationTransformer;

  @Read
  public Observation getObservation(@IdParam IdType id) {
    CaseObservation obs = observationRepository.getOne(id.getIdPartAsLong());
    return observationTransformer.transform(obs);
  }

  @Search
  public Collection<Observation> findByEncounterContext(
      @RequiredParam(name= Procedure.SP_CONTEXT) ReferenceParam contextParam) {

    return observationRepository.findAllByCaseEntityId(contextParam.getIdPartAsLong())
        .stream()
        .map(observationTransformer::transform)
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Observation.class;
  }
}
