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

@Entity
@Table(name = "users")
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

	@ManyToMany(cascade = { CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinTable(name = "user_cdss_supplier", joinColumns = { @JoinColumn(name = "username") }, inverseJoinColumns = {
			@JoinColumn(name = "cdss_supplier_id") })
	private List<CdssSupplier> cdssSuppliers;

	@Column(name = "role")
	private String role;

	public List<CdssSupplier> getCdssSuppliers() {
		return cdssSuppliers;
	}

	public String getRole() {
		return role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCdssSuppliers(List<CdssSupplier> cdssSupplier) {
		this.cdssSuppliers = cdssSupplier;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
