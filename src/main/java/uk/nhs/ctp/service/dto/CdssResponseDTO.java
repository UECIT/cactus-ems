package uk.nhs.ctp.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
public class CdssResponseDTO {

	private Long caseId;
	private Long cdssSupplierId;
	private String serviceDefinitionId;
	@JsonInclude(Include.NON_NULL)
	private List<TriageQuestion> triageQuestions;
	@JsonInclude(Include.NON_NULL)
	private String result;
	private String switchTrigger;
	private ReferralRequestDTO referralRequest;
	private List<CarePlanDTO> careAdvice;
	private ProcedureRequestDTO procedureRequest;
	private ErrorMessageDTO errorMessage;

}
