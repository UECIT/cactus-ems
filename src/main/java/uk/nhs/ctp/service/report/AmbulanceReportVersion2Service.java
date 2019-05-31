package uk.nhs.ctp.service.report;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.report.decorator.mapping.template.codedentry.ObservationToClinicalDiscriminatorTemplateMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.consent.ConsentToConsentTemplateMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.informant.PatientToPersonWithOrganizationUniversalTemplateMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.informant.RelatedPersonPOCDMT200001GB02ToRelatedEntityTemplateMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.serviceevent.BundleToServiceEventTemplateMapper;

@Service
public class AmbulanceReportVersion2Service extends OneOneOneReportService {

	@Override
	protected ReportType getReportType() {
		return ReportType.AMBULANCE_V2;
	}

	@Override
	protected Set<Class<?>> getTemplateMappingExclusions() {
		Set<Class<?>> mappingExclusions = new HashSet<>();
		
		// documentationOf
		mappingExclusions.add(BundleToServiceEventTemplateMapper.class);
		
		// Informant
		mappingExclusions.add(PatientToPersonWithOrganizationUniversalTemplateMapper.class);
		mappingExclusions.add(RelatedPersonPOCDMT200001GB02ToRelatedEntityTemplateMapper.class);
		
		// Authorization
		mappingExclusions.add(ConsentToConsentTemplateMapper.class);
		
		mappingExclusions.add(ObservationToClinicalDiscriminatorTemplateMapper.class);
		
		return mappingExclusions;
	}

}
