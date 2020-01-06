package uk.nhs.ctp.controllers;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.service.DoSService;
import uk.nhs.ctp.service.dto.HealthcareService;

@CrossOrigin
@RestController
@RequestMapping(path = "/dos")
@AllArgsConstructor
public class DoSController {

	private DoSService dosService;

	@PostMapping
	public @ResponseBody List<HealthcareService> getDoS(@RequestBody String referralRequestId) {
		return dosService.getDoS(referralRequestId);
	}

}
