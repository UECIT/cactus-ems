package uk.nhs.ctp.service.report.decorators.mapping;

import org.hl7.fhir.dstu3.model.Resource;

public interface DataMapper<RESOURCE extends Resource> {

	Class<RESOURCE> getResourceClass();
}
