package uk.nhs.ctp.service.report.decorator.flag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.BL;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation5;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PoliceFlag.Code;
import uk.nhs.ctp.utils.FlagUtil;

@Component
public class PertinentInformation3PoliceDecorator implements AmbulanceDecorator {
	
	@Value("${ambulance.request.flags[3].system}")
	private String flagSystem;
	
	@Value("${ambulance.request.flags[3].code}")
	private String flagPoliceFlagCode;
	
	@Value("${ambulance.request.flags[3].display}")
	private String flagPoliceFlagDisplay;

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02PertinentInformation5 police = document.getPertinentInformation3();

		Code code = new Code();
		code.setCode(flagPoliceFlagCode);
		code.setCodeSystem(flagSystem);
		code.setDisplayName(flagPoliceFlagDisplay);
		police.getFlag().setCode(code);

		BL policeFlag = new BL();
		policeFlag.setValue(FlagUtil.getFlagStatus(request, flagPoliceFlagCode));
		police.getFlag().setValue(policeFlag);

		document.setPertinentInformation3(police);
	}

}
