package uk.nhs.ctp.service.search;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuditSearchResultDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private Date createdDate;

}
