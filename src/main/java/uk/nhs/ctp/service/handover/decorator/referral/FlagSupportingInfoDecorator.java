package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Flag;
import org.hl7.fhir.dstu3.model.Flag.FlagStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.config.FlagProperties;

@Component
public class FlagSupportingInfoDecorator {

	@Autowired
	private FlagProperties flags;
	
	public void decorate(ReferralRequest referralRequest, CareConnectPatient patient) {
		
		flags.getFlags().stream().map(flag -> 
			new Flag()
				.setStatus(FlagStatus.ACTIVE)
				.setCode(new CodeableConcept().addCoding(flag))
				.setSubject(new Reference(patient))
				.setId("#" + flag.getCode()))
				.forEach(flag -> 
					((ReferralRequest)referralRequest
						.addContained(flag))
						.addSupportingInfo()
						.setReference(flag.getId()));
	}

}
