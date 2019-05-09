package uk.nhs.ctp.service.report.decorators.flag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorators.AmbulanceDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.BL;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation4;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02SceneSafeFlag.Code;
import uk.nhs.ctp.utils.FlagUtil;

@Component
public class PertinentInformation2SafeSceneDecorator implements AmbulanceDecorator {
	
	@Value("${ambulance.request.flags[2].system}")
	private String flagSystem;
	
	@Value("${ambulance.request.flags[2].code}")
	private String flagSafeFlagCode;
	
	@Value("${ambulance.request.flags[2].display}")
	private String flagSafeFlagDisplay;

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02PertinentInformation4 safeScene = document.getPertinentInformation2();

		Code code = new Code();
		code.setCode(flagSafeFlagCode);
		code.setCodeSystem(flagSystem);
		code.setDisplayName(flagSafeFlagDisplay);
		safeScene.getFlag().setCode(code);

		BL safeSceneFlag = new BL();
		safeSceneFlag.setValue(FlagUtil.getFlagStatus(request, flagSafeFlagCode));
		safeScene.getFlag().setValue(safeSceneFlag);

		document.setPertinentInformation2(safeScene);
	}

}
