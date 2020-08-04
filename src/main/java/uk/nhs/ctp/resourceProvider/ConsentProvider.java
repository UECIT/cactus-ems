package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Consent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConsentProvider implements IResourceProvider {

  @Read
  public Consent getConsent(@IdParam IdType id) {
    throw new ResourceNotFoundException(id);
  }

  @Search
  public Collection<Consent> findByPatientEncounter(
      @RequiredParam(name = Consent.SP_PATIENT) ReferenceParam patientParam,
      @RequiredParam(name = Consent.SP_DATA) ReferenceParam dataParam
  ) {
    return Collections.emptyList();
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Consent.class;
  }
}
