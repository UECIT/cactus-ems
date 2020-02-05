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
import lombok.Data;

@Entity
@Table(name = "case_immunization")
@Data
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

}
