package uk.nhs.ctp.service.report;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportsDTO;

public interface Reportable {

	ReportsDTO generate(ReportRequestDTO request) throws JAXBException, JsonProcessingException;
}
