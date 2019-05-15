package uk.nhs.ctp.service.report.decorators.mapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Base;

public abstract class AbstractMapper<DTO, BASE extends Base> implements Mapper<DTO, BASE> {

	@Override
	public List<DTO> map(Collection<BASE> resources) {
		return resources.stream().map(this::map).collect(Collectors.toList());
	}
	
	public abstract DTO map(BASE resource);
}
