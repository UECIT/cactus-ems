package uk.nhs.ctp.controllers;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.service.AuditService;
import uk.nhs.ctp.service.DoSService;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/dos")
@AllArgsConstructor
public class DoSController {

	private DoSService dosService;

	@GetMapping
	public @ResponseBody List<HealthcareServiceDTO> getDoS(
			@RequestParam String referralRequestId,
			@RequestParam String patientId
	)
	{
		return dosService.getDoS(referralRequestId, patientId);
	}

}
