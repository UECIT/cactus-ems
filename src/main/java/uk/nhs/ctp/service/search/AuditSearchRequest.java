package uk.nhs.ctp.service.search;

import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuditSearchRequest extends SearchRequest {

	private static final long serialVersionUID = 1L;
	
	private Date from;
	private Date to;
	
	private boolean includeClosed; 
	private boolean includeIncomplete;
	
	@JsonCreator
	public AuditSearchRequest(@JsonProperty(value="pageNumber", required=true) Integer page, 
							  @JsonProperty(value="pageSize", required=true) Integer size, 
							  @JsonProperty(value="sorts", required=false) Collection<SearchSort> sorts) {
		
		super(page, size, sorts);	
	}

	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	
	public boolean isIncludeClosed() {
		return includeClosed;
	}
	public void setIncludeClosed(boolean includeClosed) {
		this.includeClosed = includeClosed;
	}
	
	public boolean isIncludeIncomplete() {
		return includeIncomplete;
	}
	public void setIncludeIncomplete(boolean includeIncomplete) {
		this.includeIncomplete = includeIncomplete;
	}
	
}
