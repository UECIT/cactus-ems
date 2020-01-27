package uk.nhs.ctp.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import uk.nhs.ctp.enums.AuditEntryType;

@Entity
@Table(name = "audit_entry")
@Data
public class AuditEntry {

  // generic audit details
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "audit_record_id")
  @JsonIgnore
  private AuditRecord auditRecord;
  @Column(name = "type", length = 100)
  private AuditEntryType type;
  @Column(name = "created_date", length = 100)
  private Date createdDate;

  // build audit entry based on type
  @Lob
  @Column(name = "test_harness_request")
  private String testHarnessRequest;
  @Lob
  @Column(name = "cdss_service_definition_request")
  private String cdssServiceDefinitionRequest;
  @Lob
  @Column(name = "cdss_service_definition_response")
  private String cdssServiceDefinitionResponse;
  @Lob
  @Column(name = "cdss_questionnaire_request")
  private String cdssQuestionnaireRequest;
  @Lob
  @Column(name = "cdss_questionnaire_response")
  private String cdssQuestionnaireResponse;
  @Lob
  @Column(name = "test_harness_response")
  private String testHarnessResponse;
  @Lob
  @Column(name = "contained")
  private String contained;

}
