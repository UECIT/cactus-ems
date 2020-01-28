package uk.nhs.ctp.service.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorMessageDTO {

  String type;
  String display;
  String diagnostic;

}
