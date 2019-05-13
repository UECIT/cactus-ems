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

import uk.nhs.ctp.enums.AuditEntryType;

@Entity
@Table(name = "audit_entry")
public class AuditEntry {

	// generic audit details
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "audit_record_id")
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuditRecord getAuditRecord() {
		return auditRecord;
	}

	public void setAuditRecord(AuditRecord auditRecord) {
		this.auditRecord = auditRecord;
	}

	public AuditEntryType getType() {
		return type;
	}

	public void setType(AuditEntryType type) {
		this.type = type;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getTestHarnessRequest() {
		return testHarnessRequest;
	}

	public void setTestHarnessRequest(String testHarnessRequest) {
		this.testHarnessRequest = testHarnessRequest;
	}

	public String getCdssServiceDefinitionRequest() {
		return cdssServiceDefinitionRequest;
	}

	public void setCdssServiceDefinitionRequest(String cdssServiceDefinitionRequest) {
		this.cdssServiceDefinitionRequest = cdssServiceDefinitionRequest;
	}

	public String getCdssServiceDefinitionResponse() {
		return cdssServiceDefinitionResponse;
	}

	public void setCdssServiceDefinitionResponse(String cdssServiceDefinitionResponse) {
		this.cdssServiceDefinitionResponse = cdssServiceDefinitionResponse;
	}

	public String getCdssQuestionnaireRequest() {
		return cdssQuestionnaireRequest;
	}

	public void setCdssQuestionnaireRequest(String cdssQuestionnaireRequest) {
		this.cdssQuestionnaireRequest = cdssQuestionnaireRequest;
	}

	public String getCdssQuestionnaireResponse() {
		return cdssQuestionnaireResponse;
	}

	public void setCdssQuestionnaireResponse(String cdssQuestionnaireResponse) {
		this.cdssQuestionnaireResponse = cdssQuestionnaireResponse;
	}

	public String getTestHarnessResponse() {
		return testHarnessResponse;
	}

	public void setTestHarnessResponse(String testHarnessResponse) {
		this.testHarnessResponse = testHarnessResponse;
	}
	
	public String getContained() {
		return contained;
	}

	public void setContained(String contained) {
		this.contained = contained;
	}
}
