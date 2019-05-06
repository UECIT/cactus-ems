package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.codesystems.ContentType;

public class ReportsDTO {
	
	private String request;
	private String response;
	private ReportType reportType;
	private ContentType contentType;
	
	public ReportsDTO(String request, String response, ReportType reportType, ContentType contentType) {
		this.request = request;
		this.response = response;
		this.reportType = reportType;
		this.contentType = contentType;
	}

	public String getRequest() {
		return request;
	}
	
	public String getResponse() {
		return response;
	}
	
	public ReportType getReportType() {
		return reportType;
	}

	public ContentType getContentType() {
		return contentType;
	}
}

