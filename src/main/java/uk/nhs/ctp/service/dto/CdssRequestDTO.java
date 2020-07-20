package uk.nhs.ctp.service.dto;

import lombok.Data;

@Data
public class CdssRequestDTO {

	private String questionnaireId;
	private Long caseId;
	private Long cdssSupplierId;
	private String serviceDefinitionId;
	private TriageQuestion[] questionResponse;
	private SettingsDTO settings;
	private Boolean amendingPrevious;
	private String patientId;
	private String[] carePlanIds;

}
