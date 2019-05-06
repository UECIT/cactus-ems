package uk.nhs.ctp.service.search;

import java.util.Date;

public class AuditSearchResultDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private Date timestamp;
	
	public AuditSearchResultDTO(Long id, String firstName, String lastName, Date timestamp) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
