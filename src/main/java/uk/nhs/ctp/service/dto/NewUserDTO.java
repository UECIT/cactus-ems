package uk.nhs.ctp.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.nhs.ctp.entities.UserEntity;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NewUserDTO extends UserDTO {

  private String password;
  private String supplierId;

  public NewUserDTO(UserEntity entity) {
    super(entity);
  }
}
