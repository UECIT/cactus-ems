package uk.nhs.ctp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cases")
@Getter
@Setter
public class Cases {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String encounterId;

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

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "case_id")
  private List<QuestionResponse> questionResponses;

  @Column(name = "session_id")
  private String sessionId;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "case_timestamp")
  private Date timestamp;

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

  public void addImmunization(CaseImmunization immunization) {
    if (this.immunizations == null) {
      this.immunizations = new ArrayList<>();
    }
    this.immunizations.add(immunization);
  }

  public void addObservation(CaseObservation observation) {
    if (this.observations == null) {
      this.observations = new ArrayList<>();
    }
    this.observations.add(observation);
  }

  public void addParameter(CaseParameter parameter) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<>();
    }
    this.parameters.add(parameter);
  }

  public void addQuestionResponse(QuestionResponse questionResponse) {
    if (this.questionResponses == null) {
      this.questionResponses = new ArrayList<>();
    }
    this.questionResponses.add(questionResponse);
  }
}
