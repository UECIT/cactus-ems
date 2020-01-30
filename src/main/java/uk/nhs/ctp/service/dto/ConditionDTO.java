package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ConditionDTO {

  String clinicalStatus;
  String verificationStatus;
  String condition;
  String bodySite;
  String onset;
  String stageSummary;
  List<String> evidence;

}
