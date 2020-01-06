package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HealthcareService {

  long id;
  String name;
  List<String> addresses;
  boolean active;
  boolean appointmentRequired;

}
