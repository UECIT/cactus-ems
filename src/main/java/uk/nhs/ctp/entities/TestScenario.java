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
import lombok.Data;

@Entity
@Table(name = "test_scenario")
@Data
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
}
