package uk.nhs.ctp.caseSearch;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaseSearchResultDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private Date createdDate;

}
