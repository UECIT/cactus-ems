package uk.nhs.ctp.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.Reportable;

@Service
public class ReportService {
	
	@Autowired
	private Collection<Reportable> reportServices;
	
	@Autowired
	private AuditService auditService;
	
	@Autowired
	private ObjectMapper mapper;
	
	private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);
	
	public Collection<ReportsDTO> generateReports(
			ReportRequestDTO request) throws JAXBException, JsonProcessingException {
		
		Collection<ReportsDTO> reports = new ArrayList<>();
		
		for (Reportable service : reportServices) {
			try {
				reports.add(service.generate(request));
			} catch (Exception e) {
				LOG.error(MessageFormat.format("Error creating report {0} ", 
						service.getClass().getSimpleName().replace("Service", "")), e);
			}
			
		}
		
		auditService.createAuditEntry(
				request.getCaseId(), mapper.writeValueAsString(request), mapper.writeValueAsString(reports));
		
		return reports;
	}

}
