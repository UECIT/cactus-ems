package uk.nhs.ctp.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "cases")
public class Cases {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "gender")
	private String gender;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "date_of_birth")
	private Date dateOfBirth;

	@Column(name = "address")
	private String address;

	@Column(name = "nhs_number")
	private String nhsNumber;

	@ManyToOne
	@JoinColumn(name = "party_id")
	private Party party;

	@ManyToOne
	@JoinColumn(name = "skillset_id")
	private Skillset skillset;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "case_id")
	private List<CaseImmunization> immunizations;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "case_id")
	private List<CaseObservation> observations;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "case_id")
	private List<CaseMedication> medications;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "case_id")
	private List<CaseParameter> parameters;

	@Column(name = "session_id")
	private String sessionId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "case_timestamp")
	private Date timestamp;

	public Long getId() {
		return id;
	}

	public String getGender() {
		return gender;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getNhsNumber() {
		return nhsNumber;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setNhsNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public List<CaseImmunization> getImmunizations() {
		if (this.immunizations == null) {
			this.immunizations = new ArrayList<>();
		}
		return immunizations;
	}

	public List<CaseObservation> getObservations() {
		if (this.observations == null) {
			this.observations = new ArrayList<>();
		}
		return observations;
	}
	
	public List<CaseParameter> getParameters() {
		if (this.parameters == null) {
			this.parameters = new ArrayList<>();
		}
		return parameters;
	}

	public List<CaseMedication> getMedications() {
		if (this.medications == null) {
			this.medications = new ArrayList<>();
		}
		return medications;
	}

	public void addMedication(CaseMedication medication) {
		if (this.medications == null) {
			this.medications = new ArrayList<>();
		}
		this.medications.add(medication);
	}

	public void setMedications(List<CaseMedication> medications) {
		this.medications = medications;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Party getParty() {
		return party;
	}

	public Skillset getSkillset() {
		return skillset;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public void setSkillset(Skillset skillset) {
		this.skillset = skillset;
	}

	public void setImmunizations(List<CaseImmunization> immunizations) {
		this.immunizations = immunizations;
	}

	public void addImmunization(CaseImmunization immunization) {
		if (this.immunizations == null) {
			this.immunizations = new ArrayList<>();
		}
		this.immunizations.add(immunization);
	}

	public void setObservations(List<CaseObservation> observations) {
		this.observations = observations;
	}

	public void addObservation(CaseObservation observation) {
		if (this.observations == null) {
			this.observations = new ArrayList<>();
		}
		this.observations.add(observation);
	}
	
	public void setParameters(List<CaseParameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(CaseParameter parameter) {
		if (this.parameters == null) {
			this.parameters = new ArrayList<>();
		}
		this.parameters.add(parameter);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
