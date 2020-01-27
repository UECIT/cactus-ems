package uk.nhs.ctp.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

  @Id
  @Column(name = "username")
  private String username;

  @Column(name = "name")
  private String name;

  @Column(name = "password")
  private String password;

  @Column(name = "enabled")
  private boolean enabled;

  @ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER)
  @JoinTable(name = "user_cdss_supplier", joinColumns = {
      @JoinColumn(name = "username")}, inverseJoinColumns = {
      @JoinColumn(name = "cdss_supplier_id")})
  private List<CdssSupplier> cdssSuppliers;

  @Column(name = "role")
  private String role;

}
