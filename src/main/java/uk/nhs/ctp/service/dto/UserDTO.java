package uk.nhs.ctp.service.dto;

import java.util.List;

import uk.nhs.ctp.entities.UserEntity;

public class UserDTO {

	private String username;
	private String name;
	private boolean enabled;
	private String role;
	private List<CdssSupplierDTO> cdssSuppliers;

	public UserDTO(UserEntity entity) {
		this.setUsername(entity.getUsername());
		this.setName(entity.getName());
		this.setEnabled(entity.isEnabled());
		this.setRole(entity.getRole());
//		this.setCdssSuppliers(cdssSupplierService.convertToSupplierDTO(entity.getCdssSuppliers()));
	}

	public String getUsername() {
		return username;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getRole() {
		return role;
	}

	public List<CdssSupplierDTO> getCdssSuppliers() {
		return cdssSuppliers;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setCdssSuppliers(List<CdssSupplierDTO> cdssSuppliers) {
		this.cdssSuppliers = cdssSuppliers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
