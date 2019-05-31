package uk.nhs.ctp.service.report;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.report.decorator.mapping.template.authenticator.BundleToAuthenticatorAssignedEntityTemplateMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.codedentry.ObservationToFindingTemplateMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.dataenterer.RelatedPersonToPersonWithOrganisationUniversalTemplateMapper;

@Service
public class OneOneOneVersion3ReportService extends OneOneOneReportService {

	@Override
	protected ReportType getReportType() {
		return ReportType.ONE_ONE_ONE_V3;
	}
	
	@Override
	protected Set<Class<?>> getTemplateMappingExclusions() {
		Set<Class<?>> mappingExclusions = new HashSet<>();
		mappingExclusions.add(ObservationToFindingTemplateMapper.class);
		mappingExclusions.add(BundleToAuthenticatorAssignedEntityTemplateMapper.class);
		mappingExclusions.add(RelatedPersonToPersonWithOrganisationUniversalTemplateMapper.class);
		
		return mappingExclusions;
	}
}
