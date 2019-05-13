package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus;
import org.hl7.fhir.dstu3.model.Appointment.ParticipationStatus;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;

@Component
public class AppointmentSupportingInfoDecorator implements ResourceDecorator<ReferralRequest> {

	public void decorate(ReferralRequest referralRequest) {
		Appointment appointment = new Appointment();
		
		appointment.setStatus(AppointmentStatus.PROPOSED);
		appointment.setDescription("There is no appointment required");
		appointment.addParticipant();
		appointment.getParticipantFirstRep().setStatus(ParticipationStatus.TENTATIVE);
		appointment.setId("#appointment");
		
		referralRequest.addSupportingInfo().setReference("#appointment");
		referralRequest.addContained(appointment);
	}
}
