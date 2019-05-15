package uk.nhs.ctp.service.report.decorators.mapping;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.AD;
import uk.nhs.ctp.service.report.org.hl7.v3.CsPostalAddressUse;

@Component
public class AddressToADMapper extends AbstractMapper<AD, Address> {

	private Map<AddressType, CsPostalAddressUse> addressTypeToCsPostalAddressUseMap = new HashMap<>();
	
	public AddressToADMapper() {
		addressTypeToCsPostalAddressUseMap.put(AddressType.POSTAL, CsPostalAddressUse.PST);
		addressTypeToCsPostalAddressUseMap.put(AddressType.PHYSICAL, CsPostalAddressUse.PHYS);
		addressTypeToCsPostalAddressUseMap.put(AddressType.BOTH, CsPostalAddressUse.H);
	}
	
	@Override
	public AD map(Address address) {
		AD ad = new AD();
		
		for (StringType line : address.getLine()) ad.getContent().add(line.getValueAsString());
	
		ad.getContent().add(address.getCity());
		ad.getContent().add(address.getPostalCode());
		ad.getUse().add(addressTypeToCsPostalAddressUseMap.get(address.getType()));
		
		return ad;
	}
}
