package uk.nhs.ctp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "encounter")
@Data
public class EncounterEntity {

  @EmbeddedId
  private IdVersion idVersion;

  @Column
  private String patientId;

  @Column
  private String practitionerId;

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
  @JoinColumns({
      @JoinColumn(name = "encounter_id"),
      @JoinColumn(name = "encounter_version")
  })
  private List<CaseImmunization> immunizations = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumns({
      @JoinColumn(name = "encounter_id"),
      @JoinColumn(name = "encounter_version")
  })
  private List<CaseObservation> observations = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumns({
      @JoinColumn(name = "encounter_id"),
      @JoinColumn(name = "encounter_version")
  })
  private List<CaseMedication> medications = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumns({
      @JoinColumn(name = "encounter_id"),
      @JoinColumn(name = "encounter_version")
  })
  private List<CaseParameter> parameters = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumns({
      @JoinColumn(name = "encounter_id"),
      @JoinColumn(name = "encounter_version")
  })
  private List<QuestionResponse> questionResponses = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumns({
      @JoinColumn(name = "encounter_id"),
      @JoinColumn(name = "encounter_version")
  })
  private List<CaseCarePlan> carePlans = new ArrayList<>();

  @Column(name = "session_id")
  private String sessionId;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "case_timestamp")
  private Date timestamp;

  @OneToOne(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  protected ReferralRequestEntity referralRequest;

  public void setReferralRequest(ReferralRequestEntity referralRequest) {
    if (referralRequest == null) {
      if (this.referralRequest != null) {
        this.referralRequest.encounterEntity = null;
      }
    }
    else {
      referralRequest.encounterEntity = this;
    }
    this.referralRequest = referralRequest;
  }

  public void addMedication(CaseMedication medication) {
    this.medications.add(medication);
  }

  public void addImmunization(CaseImmunization immunization) {
    this.immunizations.add(immunization);
  }

  public void addObservation(CaseObservation observation) {
    this.observations.add(observation);
  }

  public void addParameter(CaseParameter parameter) {
    this.parameters.add(parameter);
  }

  public void addQuestionResponse(QuestionResponse questionResponse) {
    this.questionResponses.add(questionResponse);
  }
}
