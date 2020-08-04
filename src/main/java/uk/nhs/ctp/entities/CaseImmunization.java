package uk.nhs.ctp.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "case_immunization")
@Data
@EqualsAndHashCode(callSuper = true)
public class CaseImmunization extends SupplierPartitioned {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "code")
  private String code;

  @Column(name = "display")
  private String display;

  @Column(name = "not_given")
  private Boolean notGiven;

  @CreationTimestamp
  @Column(name = "created")
  private Date dateCreated;

  @UpdateTimestamp
  @Column(name = "updated")
  private Date dateUpdated;

}
