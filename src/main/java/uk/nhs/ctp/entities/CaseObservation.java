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
@Table(name = "case_observation")
public class CaseObservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "display")
	private String display;

	@Column(name = "data_absent_code")
	private String dataAbsentCode;

	@Column(name = "data_absent_display")
	private String dataAbsentDisplay;

	@Column(name = "value")
	private Boolean value;

	@Temporal(TemporalType.DATE)
	@Column(name = "observation_timestamp")
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

	public Boolean getValue() {
		return value;
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

	public void setValue(Boolean value) {
		this.value = value;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getDataAbsentCode() {
		return dataAbsentCode;
	}

	public void setDataAbsentCode(String dataAbsentCode) {
		this.dataAbsentCode = dataAbsentCode;
	}

	public String getDataAbsentDisplay() {
		return dataAbsentDisplay;
	}

	public void setDataAbsentDisplay(String dataAbsentDisplay) {
		this.dataAbsentDisplay = dataAbsentDisplay;
	}
}
