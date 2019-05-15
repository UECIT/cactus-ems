package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

@Component
public class ProcedureRequestBasedOnDecorator {

	public void decorate(ReferralRequest referralRequest, ProcedureRequest procedureRequest) {
		if (referralRequest.hasBasedOn()) {
			procedureRequest.setId("#procedureRequest");
			referralRequest.getBasedOnFirstRep().setReference("#procedureRequest");
			referralRequest.getBasedOnFirstRep().setResource(null);
			referralRequest.addContained(procedureRequest);
		}
	}
}
