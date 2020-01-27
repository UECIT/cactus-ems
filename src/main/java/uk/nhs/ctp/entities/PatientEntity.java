package uk.nhs.ctp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "patient")
@Data
public class PatientEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String fhirId;

  @Column(name = "address")
  private String address;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Temporal(TemporalType.DATE)
  @Column(name = "date_of_birth")
  private Date dateOfBirth;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "gender")
  private String  gender;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "nhs_number")
  private String nhsNumber;

  @Column(name = "title")
  private String title;
}
