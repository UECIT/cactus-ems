package uk.nhs.ctp.service.dto;

public class ChangePasswordDTO {

	private String username;
	private String oldPassword;
	private String newPassword;

	public String getUsername() {
		return username;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
