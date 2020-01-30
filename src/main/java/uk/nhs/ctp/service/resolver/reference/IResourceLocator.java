package uk.nhs.ctp.service.resolver.reference;

import org.hl7.fhir.instance.model.api.IBaseResource;

public interface IResourceLocator {

  IBaseResource findResource(String id);

}
