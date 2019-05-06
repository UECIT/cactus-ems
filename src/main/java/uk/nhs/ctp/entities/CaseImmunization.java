package uk.nhs.ctp.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "case_immunization")
public class CaseImmunization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "display")
	private String display;

	@Column(name = "not_given")
	private Boolean notGiven;

	@Temporal(TemporalType.DATE)
	@Column(name = "immunization_timestamp")
	private Date timestamp;

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDisplay() {
		return display;
	}

	public Boolean getNotGiven() {
		return notGiven;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public void setNotGiven(Boolean notGiven) {
		this.notGiven = notGiven;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
