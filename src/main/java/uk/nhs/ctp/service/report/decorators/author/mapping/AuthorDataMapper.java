package uk.nhs.ctp.service.report.decorators.author.mapping;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.service.report.decorators.mapping.DataMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;

public interface AuthorDataMapper<RESOURCE extends Resource> extends DataMapper<RESOURCE> {

	void map(RESOURCE resource, Organization organization, POCDMT200001GB02Author author);
}
