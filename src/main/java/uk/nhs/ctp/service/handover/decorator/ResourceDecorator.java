package uk.nhs.ctp.service.handover.decorator;

import org.hl7.fhir.dstu3.model.Resource;

public interface ResourceDecorator<RESOURCE extends Resource> {

	void decorate(RESOURCE resource);
}
