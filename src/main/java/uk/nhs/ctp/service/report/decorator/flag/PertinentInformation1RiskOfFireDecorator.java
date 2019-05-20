package uk.nhs.ctp.service.report.decorator.flag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.BL;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02FireFlag.Code;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation3;
import uk.nhs.ctp.utils.FlagUtil;

@Component
public class PertinentInformation1RiskOfFireDecorator implements AmbulanceDecorator {
	
	@Value("${ambulance.request.flags[1].system}")
	private String flagSystem;
	
	@Value("${ambulance.request.flags[1].code}")
	private String flagFireFlagCode;
	
	@Value("${ambulance.request.flags[1].display}")
	private String flagFireFlagDisplay;
	
	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02PertinentInformation3 riskOfFire = document.getPertinentInformation1();

		Code code = new Code();
		code.setCode(flagFireFlagCode);
		code.setCodeSystem(flagSystem);
		code.setDisplayName(flagFireFlagDisplay);
		riskOfFire.getFlag().setCode(code);

		BL riskOfFireFlag = new BL();
		riskOfFireFlag.setValue(FlagUtil.getFlagStatus(request, flagFireFlagCode));
		riskOfFire.getFlag().setValue(riskOfFireFlag);

		document.setPertinentInformation1(riskOfFire);
	}

}
