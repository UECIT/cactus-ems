package uk.nhs.ctp.service.search;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ObservationTrigger {

  private String code;
  private String value;
  private String effective;

}
