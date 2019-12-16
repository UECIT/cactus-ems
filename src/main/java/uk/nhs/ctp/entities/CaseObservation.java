package uk.nhs.ctp.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "case_observation")
@Data
public class CaseObservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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

  @Temporal(TemporalType.DATE)
  @Column(name = "observation_timestamp")
  private Date timestamp;
}
