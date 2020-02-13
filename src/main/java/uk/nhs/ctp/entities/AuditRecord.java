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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "audit_record")
@Data
public class AuditRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "case_id")
  private Long encounterId;
  @Column(name = "triage_complete")
  private boolean triageComplete;
  @Column(name = "created_date")
  private Date createdDate;
  @Column(name = "closed_date")
  private Date closedDate;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "auditRecord")
  private List<AuditEntry> auditEntries;

}
