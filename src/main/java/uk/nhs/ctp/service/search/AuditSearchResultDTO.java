package uk.nhs.ctp.service.search;

import java.util.Date;
import lombok.AllArgsConstructor;
import uk.nhs.ctp.entities.IdVersion;

@AllArgsConstructor
public class AuditSearchResultDTO {

	private IdVersion id;
	private String firstName;
	private String lastName;
	private Date timestamp;

}
