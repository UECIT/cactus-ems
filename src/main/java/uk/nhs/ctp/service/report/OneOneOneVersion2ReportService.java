package uk.nhs.ctp.service.report;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.report.decorator.mapping.template.ObservationToClinicalDiscriminatorTemplateMapper;

@Service
public class OneOneOneVersion2ReportService extends OneOneOneReportService {

	@Override
	protected ReportType getReportType() {
		return ReportType.ONE_ONE_ONE_V2;
	}

	@Override
	protected Set<Class<?>> getTemplateMappingExclusions() {
		Set<Class<?>> mappingExclusions = new HashSet<>();
		mappingExclusions.add(ObservationToClinicalDiscriminatorTemplateMapper.class);
		
		return mappingExclusions;
	}

}
