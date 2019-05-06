package uk.nhs.ctp.service.dto;

import uk.nhs.ctp.entities.UserEntity;

public class NewUserDTO extends UserDTO {

	public NewUserDTO(UserEntity entity) {
		super(entity);
	}

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
