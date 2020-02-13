package uk.nhs.ctp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "referral_request")
@Data
public class ReferralRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "encounter_id"),
      @JoinColumn(name = "encounter_version")
  })
  @ToString.Exclude()
  protected EncounterEntity encounterEntity;

  @Lob
  @Column
  @JsonRawValue
  private String resource;

  public void setEncounterEntity(EncounterEntity encounterEntity) {
    encounterEntity.setReferralRequest(this);
  }
}
