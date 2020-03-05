package uk.nhs.ctp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeDTO {

  private String code;
  private String display;
  private String system;
}
