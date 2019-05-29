package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectLocation;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145222GB02Place;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145222GB02Place.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.TN;

@Component
public class LocationToCOCDTP145222GB02PlaceMapper extends AbstractMapper<COCDTP145222GB02Place, CareConnectLocation> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Override
	public COCDTP145222GB02Place map(CareConnectLocation location) {
		COCDTP145222GB02Place place = new COCDTP145222GB02Place();
		place.setClassCode(place.getClassCode());
		place.setDeterminerCode(place.getDeterminerCode());
		place.setAddr(addressToADMapper.map(location.getAddress()));
		
		TN name = new TN();
		name.getContent().add(location.getName());
		place.setName(name);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145222GB02#location");
		place.setTemplateId(templateId);
		
		return place;
	}

}
