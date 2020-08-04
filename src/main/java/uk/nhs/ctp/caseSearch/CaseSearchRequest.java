package uk.nhs.ctp.caseSearch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseSearchRequest extends SearchRequest {

	private static final long serialVersionUID = 1L;
	
	private Date from;
	private Date to;
	
	private boolean includeClosed; 
	private boolean includeIncomplete;
	
	@JsonCreator
	public CaseSearchRequest(@JsonProperty(value="pageNumber", required=true) Integer page,
							  @JsonProperty(value="pageSize", required=true) Integer size, 
							  @JsonProperty(value="sorts") Collection<SearchSort> sorts) {
		
		super(page, size, sorts);	
	}
}
