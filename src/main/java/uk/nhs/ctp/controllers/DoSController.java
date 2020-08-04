package uk.nhs.ctp.controllers;

import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;
import static uk.nhs.cactus.common.audit.model.AuditProperties.OPERATION_TYPE;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cactus.common.audit.AuditService;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.service.DoSService;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/dos")
@RequiredArgsConstructor
public class DoSController {

	private final DoSService dosService;
	private final AuditService auditService;

	@GetMapping
	public @ResponseBody List<HealthcareServiceDTO> getDoS(
			@RequestParam String referralRequestId,
			@RequestParam String patientId
	)
	{
		var requestId = UUID.randomUUID().toString();
		auditService.addAuditProperty(OPERATION_TYPE, OperationType.CHECK_SERVICES.getName());
		auditService.addAuditProperty(INTERACTION_ID, requestId);

		return dosService.getDoS(referralRequestId, patientId, requestId);
	}

}
