package uk.nhs.ctp.service.handover.decorator;

import org.hl7.fhir.dstu3.model.Resource;

public interface ResourceDecorator<R extends Resource, V> {

	void decorate(R resource, V dataObject);
}
