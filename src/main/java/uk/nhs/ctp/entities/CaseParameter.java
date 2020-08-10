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
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "case_parameter")
@Data
@EqualsAndHashCode(callSuper = true)
public class CaseParameter extends SupplierPartitioned {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "reference")
	private String reference;

	@Temporal(TemporalType.DATE)
	@Column(name = "parameter_timestamp")
	private Date timestamp;

	@Column(name = "deleted")
	private boolean deleted;
}
