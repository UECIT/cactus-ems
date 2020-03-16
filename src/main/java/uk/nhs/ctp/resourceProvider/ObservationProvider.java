package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.Collection;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.ObservationService;

@Component
@AllArgsConstructor
public class ObservationProvider implements IResourceProvider {

  private final ObservationService observationService;

  @Read
  public Observation getObservation(@IdParam IdType id) {
    return observationService.getOne(id);
  }

  @Search
  public Collection<Observation> findByEncounterContext(
      @RequiredParam(name= Procedure.SP_CONTEXT) ReferenceParam contextParam) {

    return observationService.getByCaseId(contextParam.getIdPartAsLong());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Observation.class;
  }
}
