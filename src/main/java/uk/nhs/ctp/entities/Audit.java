package uk.nhs.ctp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import uk.nhs.ctp.enums.AuditType;

@Entity
@Table(name = "audit")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: delete this quickly
public class Audit extends SupplierPartitioned {

  @JsonIgnore
  private boolean storable;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "case_id")
  private Long caseId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "audit")
  private List<AuditEntry> auditEntries;

  @Column(name = "type", length = 100)
  private AuditType type;

  @Column(name = "request_method")
  @Lob
  private String requestMethod;

  @Column(name = "request_url")
  @Lob
  private String requestUrl;

  @Column(name = "request_headers")
  @Lob
  private String requestHeaders;

  @Column(name = "request_body")
  @Lob
  private String requestBody;

  @Column(name = "response_status")
  private int responseStatus;

  @Column(name = "response_status_text")
  private String responseStatusText;

  @Column(name = "response_headers")
  @Lob
  private String responseHeaders;

  @Column(name = "response_body")
  @Lob
  private String responseBody;
}
