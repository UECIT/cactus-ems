package uk.nhs.ctp.service.report.decorator.mapping;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.text.WordUtils;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.AD;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.City;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.Country;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.County;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.PostalCode;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.StreetAddressLine;
import uk.nhs.ctp.service.report.org.hl7.v3.CsPostalAddressUse;

@Component
public class AddressToADMapper extends AbstractMapper<AD, Address> {

	private Map<AddressUse, CsPostalAddressUse> addressUseToCsPostalAddressUseMap = new HashMap<>();
	
	public AddressToADMapper() {
		addressUseToCsPostalAddressUseMap.put(AddressUse.HOME, CsPostalAddressUse.H);
		addressUseToCsPostalAddressUseMap.put(AddressUse.WORK, CsPostalAddressUse.WP);
	}
	
	@Override
	public AD map(Address address) {
		AD ad = new AD();
		
		address.getLine().stream().forEach(line -> 
			addAddressPart(line.getValueAsString(), StreetAddressLine.class, ad));

		addAddressPart(address.getCity(), City.class, ad);
		addAddressPart(address.getDistrict(), County.class, ad);
		addAddressPart(address.getPostalCode(), PostalCode.class, ad);
		addAddressPart(address.getCountry(), Country.class, ad);

		ad.getUse().add(addressUseToCsPostalAddressUseMap.get(address.getUse()));

		return ad;
	}
	
	private void addAddressPart(String part, Class<?> containerClass, AD targetAddress) {
		if (part != null) {
			targetAddress.getContent().add(new JAXBElement<>(new QName("urn:hl7-org:v3",
					WordUtils.uncapitalize(containerClass.getSimpleName())), String.class, part));
		}
		
	}
}
