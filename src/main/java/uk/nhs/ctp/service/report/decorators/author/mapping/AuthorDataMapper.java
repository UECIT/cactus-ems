package uk.nhs.ctp.service.report.decorators.author.mapping;

import java.util.function.BiConsumer;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.service.report.decorators.mapping.DataMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;

public interface AuthorDataMapper<DATA, RESOURCE extends Resource> extends DataMapper<DATA, RESOURCE> {

	DATA map(RESOURCE resource, Organization organization);
	
	BiConsumer<POCDMT200001GB02Author, Object> mappingFunction();
}
