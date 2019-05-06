package uk.nhs.ctp.service.resolver;

import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.service.dto.CdssResult;

public interface ResponseResolver<RESOURCE extends Resource> {

	CdssResult resolve(Resource resource, CdssSupplier cdssSupplier);
	
	Class<RESOURCE> getResourceClass();
}
