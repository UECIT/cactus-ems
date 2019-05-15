package uk.nhs.ctp.service.handover.decorator.referral;

import java.util.Calendar;
import java.util.Date;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Consent;
import org.hl7.fhir.dstu3.model.Consent.ConsentDataMeaning;
import org.hl7.fhir.dstu3.model.Consent.ConsentState;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;

@Component
public class ConsentDecorator implements ResourceDecorator<ReferralRequest, AuditEntry> {

	@Override
	public void decorate(ReferralRequest referralRequest, AuditEntry auditEntry) {
		Consent consent = new Consent();
		consent.setId("#consent");
		
		consent.setIdentifier(null);
		consent.setStatus(ConsentState.ACTIVE);
		consent.addCategory().addCoding()
			.setCode("59284-0")
			.setDisplay("Patient Consent")
			.setSystem("http://Ioinc.org");
		consent.setPatient(referralRequest.getSubject());
		consent.setPeriod(getPeriod());
		consent.setDateTime(new Date());
		consent.addConsentingParty(referralRequest.getSubject());
		
		// Who | What controlled by this consent
		consent.addActor()
			.setRole(new CodeableConcept()
					.addCoding(new Coding()
							.setCode("PROV")
							.setDisplay("healthcare provider")
							.setSystem("http://hl7.org/fhir/v3/RoleClass")))
			.setReference(referralRequest.getRecipientFirstRep());
		
		consent.addAction().addCoding()
			.setCode("access")
			.setDisplay("Access")
			.setSystem("http://hl7.org/fhir/consentaction");
		consent.addAction().addCoding()
			.setCode("use")
			.setDisplay("Use")
			.setSystem("http://hl7.org/fhir/consentaction");
		consent.addAction().addCoding()
			.setCode("disclose")
			.setDisplay("Disclose")
			.setSystem("http://hl7.org/fhir/consentaction");
		consent.addOrganization(referralRequest.getRequester().getOnBehalfOf());
		consent.setSource(new Reference(new QuestionnaireResponse()));
		consent.addPolicy()
			.setAuthority("http://uecdi-ems-poc.com/")
			.setUri("http://uecdi-ems-poc.com/privacy-policy");
		consent.setPolicyRule("http://uecdi-ems-poc.com/privacy-policy-rules");
		consent.addSecurityLabel()
			.setCode("M")
			.setDisplay("moderate")
			.setSystem("http://hl7.org/fhir/v3/Confidentiality");
		consent.addPurpose()
			.setCode("HOPERAT")
			.setDisplay("healthcare operations")
			.setSystem("http://hl7.org/fhir/v3/ActReason");
		consent.setDataPeriod(getPeriod());
		consent.addData()
			.setMeaning(ConsentDataMeaning.DEPENDENTS)
			.setReference(new Reference(referralRequest));
		
		referralRequest.addSupportingInfo(new Reference(consent));
		referralRequest.addContained(consent);
	}
	
	private Period getPeriod() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, 1); // to get previous year add -1
		Date nextYear = cal.getTime();
		return new Period().setStart(today).setEnd(nextYear);
	}

}
