package uk.nhs.ctp.service.report.decorator.mapping;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.text.WordUtils;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.AD;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.Country;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.County;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.City;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.PostalCode;
import uk.nhs.ctp.service.report.org.hl7.v3.AD.StreetAddressLine;
import uk.nhs.ctp.service.report.org.hl7.v3.ADXP;
import uk.nhs.ctp.service.report.org.hl7.v3.CsPostalAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.TEL;

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
		
		address.getLine().stream().forEach(line -> 
			addAddressPart(line.getValueAsString(), new StreetAddressLine(), StreetAddressLine.class, ad));

		addAddressPart(address.getCity(), new City(), City.class, ad);
		addAddressPart(address.getDistrict(), new County(), County.class, ad);
		addAddressPart(address.getPostalCode(), new PostalCode(), PostalCode.class, ad);
		addAddressPart(address.getCountry(), new Country(), Country.class, ad);
		
		ad.getUse().add(addressTypeToCsPostalAddressUseMap.get(address.getType()));
		
		return ad;
	}
	
	private <T extends ADXP> void addAddressPart(String part, T partContainer, Class<T> containerClass, AD targetAddress) {
		if (part != null) {
			TEL valueContainer = new TEL();
			valueContainer.setValue(part);
			partContainer.setReference(valueContainer);
			
			targetAddress.getContent().add(new JAXBElement<T>(
					new QName(WordUtils.uncapitalize(containerClass.getSimpleName())), containerClass, partContainer));
		}
		
	}
}
