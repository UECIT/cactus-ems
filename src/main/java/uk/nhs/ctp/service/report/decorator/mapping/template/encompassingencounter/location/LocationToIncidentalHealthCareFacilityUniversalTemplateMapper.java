package uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.location;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;

import resources.CareConnectLocation;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.LocationToCOCDTP145222GB02PlaceMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145222GB02HealthCareFacility;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145222GB02HealthCareFacility.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145222GB02Place;
import uk.nhs.ctp.service.report.org.hl7.v3.HealthCareFacilityAware;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequired;

public abstract class LocationToIncidentalHealthCareFacilityUniversalTemplateMapper <CONTAINER extends HealthCareFacilityAware>
		implements TemplateMapper<CareConnectLocation, CONTAINER> {

	@Autowired
	private LocationToCOCDTP145222GB02PlaceMapper locationToPlaceMapper;
	
	@Override
	public void map(CareConnectLocation location, CONTAINER container, ReportRequestDTO request) {
		COCDTP145222GB02HealthCareFacility healthCareFacility = new COCDTP145222GB02HealthCareFacility();
		healthCareFacility.setClassCode(healthCareFacility.getClassCode());
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		healthCareFacility.setTemplateId(templateId);
		
		IINPfITOidRequired id = new IINPfITOidRequired();
		id.setRoot("2.16.840.1.113883.2.1.3.2.4.18.41");
		id.setExtension("200001025758");
		healthCareFacility.getId().add(id);

		healthCareFacility.setLocation(new JAXBElement<COCDTP145222GB02Place>(
				new QName("urn:hl7-org:v3", "location"), COCDTP145222GB02Place.class, locationToPlaceMapper.map(location)));
		
		container.setCOCDTP145222GB02HealthCareFacility(healthCareFacility);
	}

	@Override
	public Class<CareConnectLocation> getResourceClass() {
		return CareConnectLocation.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145222GB02#HealthCareFacility";
	}

}
