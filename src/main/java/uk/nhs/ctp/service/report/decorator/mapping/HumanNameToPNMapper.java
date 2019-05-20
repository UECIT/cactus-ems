package uk.nhs.ctp.service.report.decorator.mapping;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;

@Component
public class HumanNameToPNMapper extends AbstractMapper<PN, HumanName> {

	private Map<NameUse, CsEntityNameUse> nameUseToEntityNameUseMap = new HashMap<>();
	
	public HumanNameToPNMapper() {
		nameUseToEntityNameUseMap.put(NameUse.OFFICIAL, CsEntityNameUse.L);
	}
	
	@Override
	public PN map(HumanName humanName) {
		PN pn = new PN();
		pn.getUse().add(CsEntityNameUse.L);
		pn.getContent().add(MessageFormat.format("{0} {1} {2}", 
				humanName.getPrefixAsSingleString(), humanName.getGivenAsSingleString(), humanName.getFamily()));

		return pn;
	}
}
