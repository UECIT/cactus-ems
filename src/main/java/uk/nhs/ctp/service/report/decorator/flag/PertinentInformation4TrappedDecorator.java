package uk.nhs.ctp.service.report.decorator.flag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.BL;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation6;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TrappedFlag.Code;
import uk.nhs.ctp.utils.FlagUtil;

@Component
public class PertinentInformation4TrappedDecorator implements AmbulanceDecorator {
	
	@Value("${ambulance.request.flags[4].system}")
	private String flagSystem;
	
	@Value("${ambulance.request.flags[4].code}")
	private String flagTrappedFlagCode;
	
	@Value("${ambulance.request.flags[4].display}")
	private String flagTrappedFlagDisplay;

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02PertinentInformation6 trapped = document.getPertinentInformation4();

		Code code = new Code();
		code.setCode(flagTrappedFlagCode);
		code.setCodeSystem(flagSystem);
		code.setDisplayName(flagTrappedFlagDisplay);
		trapped.getFlag().setCode(code);

		BL trappedFlag = new BL();
		trappedFlag.setValue(FlagUtil.getFlagStatus(request, flagTrappedFlagCode));
		trapped.getFlag().setValue(trappedFlag);

		document.setPertinentInformation4(trapped);
	}

}
