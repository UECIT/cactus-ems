package uk.nhs.ctp.service.dto;


import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelectedServiceRequestDTO {
  private Long caseId;
  private String selectedServiceId;
  private List<CodeDTO> serviceTypes;
}
