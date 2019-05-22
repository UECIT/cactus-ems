package uk.nhs.ctp.service.report;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.nhs.ctp.service.dto.ReportType;

@Service
public class OneOneOneVersion3ReportService extends OneOneOneReportService {

	@Override
	protected ReportType getReportType() {
		return ReportType.ONE_ONE_ONE_V3;
	}

	@Override
	protected Set<Class<?>> getTemplateMappingExclusions() {
		return new HashSet<>();
	}
}
