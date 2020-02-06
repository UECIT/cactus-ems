package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EncounterHandoverDTO {

  String encounterId;
  List<String> observations;
  String patientId;
  String patientName;
  String patientAddress;

}
