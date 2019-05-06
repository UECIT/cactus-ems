package uk.nhs.ctp.entities;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "audit_record")
public class AuditRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "case_id")
	private Long caseId;
	@Column(name = "triage_complete")
	private boolean triageComplete;
	@Column(name = "created_date")
	private Date createdDate;
	@Column(name = "closed_date")
	private Date closedDate;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "audit_record_id")
	private List<AuditEntry> auditEntries;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public boolean isTriageComplete() {
		return triageComplete;
	}

	public void setTriageComplete(boolean triageComplete) {
		this.triageComplete = triageComplete;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public List<AuditEntry> getAuditEntries() {
		return auditEntries;
	}

	public void setAuditEntries(List<AuditEntry> auditEntries) {
		this.auditEntries = auditEntries;
	}

}
