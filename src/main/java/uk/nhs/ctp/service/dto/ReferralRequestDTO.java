package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReferralRequestDTO {

	String status;
	String priority;
	String occurrence;
	String action;
	String description;
	ConditionDTO reasonReference;
	List<ConditionDTO> supportingInfo;
	String relevantHistory;
	String contextReference;
	String resourceId;

}
