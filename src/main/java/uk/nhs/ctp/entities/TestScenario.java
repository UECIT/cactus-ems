package uk.nhs.ctp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "test_scenario")
public class TestScenario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "patient_id")
	private PatientEntity patient;

	@ManyToOne
	@JoinColumn(name = "party_id")
	private Party party;

	@ManyToOne
	@JoinColumn(name = "skillset_id")
	private Skillset skillset;

	@Column(name = "test_case_summary")
	private String testCaseSummary;

	public Long getId() {
		return id;
	}

	public PatientEntity getPatient() {
		return patient;
	}

	public Party getParty() {
		return party;
	}

	public Skillset getSkillset() {
		return skillset;
	}

	public String getTestCaseSummary() {
		return testCaseSummary;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPatient(PatientEntity patient) {
		this.patient = patient;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public void setSkillset(Skillset skillset) {
		this.skillset = skillset;
	}

	public void setTestCaseSummary(String testCaseSummary) {
		this.testCaseSummary = testCaseSummary;
	}
}
