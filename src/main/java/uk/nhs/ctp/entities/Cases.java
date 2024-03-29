package uk.nhs.ctp.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "cases", indexes = @Index(columnList = "supplierId"))
@Data
@EqualsAndHashCode(callSuper = true)
public class Cases extends SupplierPartitioned {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String patientId;

  @Column
  private String practitionerId;

  @Column(name = "firstName")
  private String firstName;

  @Column(name = "lastName")
  private String lastName;

  @Column(name = "nhs_number")
  private String nhsNumber;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "case_id")
  private List<CaseParameter> parameters = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "case_id")
  private List<QuestionResponse> questionResponses = new ArrayList<>();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "closed_date")
  private Date closedDate;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "case_id")
  private List<CompositionEntity> compositions = new ArrayList<>();

  @Column(name = "triage_complete")
  private Boolean triageComplete;

  public void addComposition(CompositionEntity composition) {
    this.compositions.add(composition);
    composition.setCaseEntity(this);
  }

  public void addParameter(CaseParameter parameter) {
    this.parameters.add(parameter);
  }

  public void addQuestionResponse(QuestionResponse questionResponse) {
    this.questionResponses.add(questionResponse);
  }
}
