package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.Collection;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.ProcedureService;

@Component
@AllArgsConstructor
public class ProcedureProvider implements IResourceProvider {

  private ProcedureService procedureService;

  @Search
  public Collection<Procedure> findByEncounterContext(@RequiredParam(name= Procedure.SP_CONTEXT)
      ReferenceParam contextParam) {
    return procedureService.getByCaseId(contextParam.getIdPartAsLong());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Procedure.class;
  }
}
