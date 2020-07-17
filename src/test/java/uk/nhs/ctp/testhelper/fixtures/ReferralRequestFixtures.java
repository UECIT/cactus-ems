package uk.nhs.ctp.testhelper.fixtures;

import java.util.Calendar;
import java.util.GregorianCalendar;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;

@UtilityClass
public class ReferralRequestFixtures {

  public ReferralRequest fhirReferralRequest() {
    ReferralRequest rr = new ReferralRequest()
        .setContext(new Reference("context/reference"))
        .setSubject(new Reference("subject/reference"))
        .setStatus(ReferralRequestStatus.ACTIVE)
        .setPriority(ReferralPriority.ROUTINE)
        .setOccurrence(new Period()
            .setStart(new GregorianCalendar(2001, Calendar.FEBRUARY, 4, 3, 2, 1).getTime())
            .setEnd(new GregorianCalendar(2005, Calendar.JUNE, 1, 2, 3, 4).getTime()))
        .addReasonCode(new CodeableConcept(new Coding("reasonsys", "reasoncode", "reasondisplay")))
        .setDescription("Referral Description")
        .addRelevantHistory(new Reference("history/reference").setDisplay("history display"))
        .addReasonReference(new Reference("reason/reference"))
        .addSupportingInfo(new Reference("ProcedureRequest/reference"))
        .addSupportingInfo(new Reference("Condition/reference"));

    rr.setId("referral/id");
    return rr;
  }

}
