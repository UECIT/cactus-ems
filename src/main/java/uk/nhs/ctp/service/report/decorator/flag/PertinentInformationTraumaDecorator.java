package uk.nhs.ctp.service.report.decorator.flag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.BL;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation2;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TraumaFlag.Code;
import uk.nhs.ctp.utils.FlagUtil;

@Component
public class PertinentInformationTraumaDecorator implements AmbulanceDecorator {
	
	@Value("${ambulance.request.flags[0].system}")
	private String flagSystem;
	
	@Value("${ambulance.request.flags[0].code}")
	private String flagTraumaFlagCode;
	
	@Value("${ambulance.request.flags[0].display}")
	private String flagTraumaFlagDisplay;

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02PertinentInformation2 trauma = document.getPertinentInformation();

		Code code = new Code();
		code.setCode(flagTraumaFlagCode);
		code.setCodeSystem(flagSystem);
		code.setDisplayName(flagTraumaFlagDisplay);
		trauma.getFlag().setCode(code);

		BL traumaFlag = new BL();
		traumaFlag.setValue(FlagUtil.getFlagStatus(request,flagTraumaFlagCode));
		trauma.getFlag().setValue(traumaFlag);

		document.setPertinentInformation(trauma);
	}

}
