package uk.nhs.ctp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "referral_request")
@Data
public class ReferralRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "case_id")
  @ToString.Exclude()
  protected Cases caseEntity;

  @Lob
  @Column
  @JsonRawValue
  private String resource;

  @CreationTimestamp
  @Column(name = "created")
  private Date dateCreated;

  @UpdateTimestamp
  @Column(name = "updated")
  private Date dateUpdated;

  public void setCaseEntity(Cases caseEntity) {
    caseEntity.setReferralRequest(this);
  }
}
