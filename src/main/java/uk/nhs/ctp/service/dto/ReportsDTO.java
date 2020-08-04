package uk.nhs.ctp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.nhs.ctp.enums.ContentType;

@AllArgsConstructor
@Getter
@Builder
public class ReportsDTO {
	
	private String request;
	@Setter
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
}

