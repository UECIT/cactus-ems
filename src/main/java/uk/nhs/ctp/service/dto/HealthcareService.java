package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HealthcareService {

  long id;
  String name;
  String endpoint;
  String description;
  boolean active;
  boolean appointmentRequired;
  String phoneNumber;
  String email;
  List<String> provision;
  List<String> availableTimes;
  List<String> notAvailableTimes;

}
