package uk.nhs.ctp.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordDTO {

  private String username;
  private String oldPassword;
  private String newPassword;

}
