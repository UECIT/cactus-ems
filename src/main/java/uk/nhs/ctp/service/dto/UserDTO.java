package uk.nhs.ctp.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.nhs.ctp.entities.UserEntity;

@Data
@NoArgsConstructor
public class UserDTO {

  private String username;
  private String name;
  private boolean enabled;
  private String role;

  public UserDTO(UserEntity entity) {
    this.setUsername(entity.getUsername());
    this.setName(entity.getName());
    this.setEnabled(entity.isEnabled());
    this.setRole(entity.getRole());
  }

}
