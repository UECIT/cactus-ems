package uk.nhs.ctp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "case_observation")
@Data
public class CaseObservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(optional = false)
  @JoinColumn(name = "case_id")
  @ToString.Exclude()
  private Cases caseEntity;

  @Column(name = "system")
  private String system;

  @Column(name = "code")
  private String code;

  @Column(name = "display")
  private String display;

  @Column(name = "data_absent_system")
  private String dataAbsentSystem;

  @Column(name = "data_absent_code")
  private String dataAbsentCode;

  @Column(name = "data_absent_display")
  private String dataAbsentDisplay;

  @Column(name = "value_system")
  private String valueSystem;

  @Column(name = "value_code")
  private String valueCode;

  @Column(name = "value_display")
  private String valueDisplay;

  @CreationTimestamp
  @Column(name = "created")
  private Date dateCreated;

  @UpdateTimestamp
  @Column(name = "updated")
  private Date dateUpdated;
}
