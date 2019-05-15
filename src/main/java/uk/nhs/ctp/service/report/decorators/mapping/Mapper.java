package uk.nhs.ctp.service.report.decorators.mapping;

import java.util.Collection;
import java.util.List;

import org.hl7.fhir.dstu3.model.Base;

public interface Mapper<DTO, BASE extends Base> {

	DTO map(BASE resource);
	
	List<DTO> map(Collection<BASE> resources);
}
