package uk.nhs.ctp.service.report.decorators.tracker.mapping;

import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.service.report.decorators.mapping.DataMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;

public interface TrackerDataMapper<RESOURCE extends Resource> extends DataMapper<RESOURCE> {

	void map(RESOURCE resource, POCDMT200001GB02Tracker tracker);
}
