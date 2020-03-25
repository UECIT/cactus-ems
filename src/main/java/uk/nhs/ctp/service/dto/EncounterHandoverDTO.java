package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EncounterHandoverDTO {

  private String encounterId;
  private String encounterStart;
  private String encounterEnd;
  private List<String> observations;
  private String patientId;
  private String patientName;
  private String patientAddress;

}
