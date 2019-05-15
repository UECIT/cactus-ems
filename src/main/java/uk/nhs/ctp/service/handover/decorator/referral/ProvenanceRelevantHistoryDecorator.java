package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

@Component
public class ProvenanceRelevantHistoryDecorator {

	public void decorate(ReferralRequest referralRequest, Provenance provenance) {
		if (referralRequest.hasRelevantHistory()) {
			provenance.setId("#provenance");
			referralRequest.getRelevantHistoryFirstRep().setReference("#provenance");
			referralRequest.getRelevantHistoryFirstRep().setResource(null);
			referralRequest.addContained(provenance);
		}
	}
}
