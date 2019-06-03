package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.codesystems.ContentType;

public class ReportsDTO {
	
	private String request;
	private String response;
	private ReportType reportType;
	private ContentType contentType;
	private String documentId;
	
	public ReportsDTO(String request, ReportType reportType, ContentType contentType, String documentId) {
		this(request, null, reportType, contentType, documentId);
	}
	
	public ReportsDTO(String request, String response, ReportType reportType, ContentType contentType) {
		this(request, response, reportType, contentType, null);
	}
	
	public ReportsDTO(String request, String response, ReportType reportType, ContentType contentType, String documentId) {
		this.request = request;
		this.response = response;
		this.reportType = reportType;
		this.contentType = contentType;
		this.documentId = documentId;
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

	public String getDocumentId() {
		return documentId;
	}
}

