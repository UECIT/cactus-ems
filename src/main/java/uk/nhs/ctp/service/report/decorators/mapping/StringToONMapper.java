package uk.nhs.ctp.service.report.decorators.mapping;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;

@Component
public class StringToONMapper {

	public ON map(String string) {
		ON on = new ON();
		on.getContent().add(string);
		//currently no mapping from FHIR organization name use to report name use so just set to (L)egal
		on.getUse().add(CsEntityNameUse.L);
		
		return on;
	}
}
