package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus;
import org.hl7.fhir.dstu3.model.Appointment.ParticipationStatus;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;

@Component
public class AppointmentSupportingInfoDecorator implements ResourceDecorator<ReferralRequest, AuditEntry> {

	public void decorate(ReferralRequest referralRequest, AuditEntry auditEntry) {
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
