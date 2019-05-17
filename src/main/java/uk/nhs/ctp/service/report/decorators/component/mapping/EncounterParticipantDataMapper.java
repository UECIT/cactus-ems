package uk.nhs.ctp.service.report.decorators.component.mapping;

import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.service.report.decorators.mapping.DataMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncompassingEncounter;

public interface EncounterParticipantDataMapper<RESOURCE extends Resource> extends DataMapper<RESOURCE> {

	void map(RESOURCE resource, COCDTP146232GB01EncompassingEncounter encompassingEncounter);
}
