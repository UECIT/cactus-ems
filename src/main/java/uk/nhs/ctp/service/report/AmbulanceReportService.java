package uk.nhs.ctp.service.report;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.hl7.fhir.dstu3.model.codesystems.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.decorators.AmbulanceDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.BL;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.Flag;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITUuidMandatory;
import uk.nhs.ctp.service.report.org.hl7.v3.ObjectFactory;
import uk.nhs.ctp.service.report.org.hl7.v3.PertinentInformation;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest.EffectiveTime;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02EncounterEvent;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02FireFlag;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation2;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation3;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation4;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation5;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation6;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PoliceFlag;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Reason;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02SceneSafeFlag;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TrappedFlag;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TraumaFlag;
import uk.nhs.ctp.utils.ConversionUtil;

@Service
public class AmbulanceReportService implements Reportable {
	
	@Autowired
	private List<AmbulanceDecorator> decorators;
	
	private ObjectFactory objectFactory = new ObjectFactory();
	
	@Override
	public ReportsDTO generate(ReportRequestDTO request) throws JAXBException {
		return new ReportsDTO(ConversionUtil.convertToXml(
				objectFactory.createAmbulanceRequest(generateAmbulanceReport(request)), 
				"uk.nhs.ctp.service.report.org.hl7.v3"), null, ReportType.AMBULANCE, ContentType.XML);
	}
	
	private REPCMT200001GB02AmbulanceRequest generateAmbulanceReport(ReportRequestDTO request) {
		
		REPCMT200001GB02AmbulanceRequest ambulanceRequestReport = objectFactory.createREPCMT200001GB02AmbulanceRequest();
		// TODO populate an Ambulance Request Report.
		
		ambulanceRequestReport.setClassCode(ambulanceRequestReport.getClassCode());
		ambulanceRequestReport.setMoodCode(ambulanceRequestReport.getMoodCode());
		
		// The HL7 attribute code uses a code from the AmbulanceRequestTypeSnCT to describe the type of request message.
		CV code = new CV();
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.15");
		code.setCode("828801000000101");
		ambulanceRequestReport.setCode(new CV());
		
		// The HL7 attribute effectiveTime is used to define when the request for an ambulance was made.
		EffectiveTime effectiveTime = new EffectiveTime();
		effectiveTime.setValue(new Timestamp(new Date().getTime()).toString());
		ambulanceRequestReport.setEffectiveTime(effectiveTime);
		
		// The HL7 attribute id holds a unique identifier for this care provision request.
		IINPfITUuidMandatory identifier = new IINPfITUuidMandatory();
		identifier.setRoot("1234-1234-1234-1234");
		ambulanceRequestReport.setId(identifier);
		
		for (AmbulanceDecorator decorator : decorators) {
			decorator.decorate(ambulanceRequestReport, request);	
		}
		
		// The contact details given by the responsible party as a contact point for the Ambulance Request process. CAN BE NULL
		ambulanceRequestReport.setCallBackContact(null);
		
		// This class is a information recipient class - It is used for the list of recipients of the ambulance request message.
//		REPCMT200001GB02PrimaryInformationRecipient primaryInformationRecipient = new REPCMT200001GB02PrimaryInformationRecipient();
//		primaryInformationRecipient.setTypeCode(primaryInformationRecipient.getTypeCode());
//		ambulanceRequestReport.getInformationRecipient().add(primaryInformationRecipient);

		// This class is a relationship of pertinent information. - It is used to indicate information about whether the patient has suffered trauma pertinent to the ambulance request.
		ambulanceRequestReport.setPertinentInformation((REPCMT200001GB02PertinentInformation2) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation2(), 
						new REPCMT200001GB02TraumaFlag(), new REPCMT200001GB02PertinentInformation2.SeperatableInd()));
		
		// This class is a relationship of pertinent information. - It is used to indicate information about whether there is a risk of fire at the incident scene pertinent to the ambulance request.
		ambulanceRequestReport.setPertinentInformation1((REPCMT200001GB02PertinentInformation3) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation3(), 
						new REPCMT200001GB02FireFlag(), new REPCMT200001GB02PertinentInformation3.SeperatableInd()));
		
		// This class is a relationship of pertinent information. - It is used to indicate information about whether the scene is safe pertinent to the ambulance request.
		ambulanceRequestReport.setPertinentInformation2((REPCMT200001GB02PertinentInformation4) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation4(), 
						new REPCMT200001GB02SceneSafeFlag(), new REPCMT200001GB02PertinentInformation4.SeperatableInd()));
		
		// This class is a relationship of pertinent information. - It is used to indicate information about whether police are in attendance at the incident scen e pertinent to the ambulance request.
		ambulanceRequestReport.setPertinentInformation3((REPCMT200001GB02PertinentInformation5) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation5(), 
						new REPCMT200001GB02PoliceFlag(), new REPCMT200001GB02PertinentInformation5.SeperatableInd()));
		
		// This class is a relationship of pertinent information. - It is used to indicate information about whether the patient is trapped pertinent to the ambulance request.
		ambulanceRequestReport.setPertinentInformation4((REPCMT200001GB02PertinentInformation6) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation6(), 
						new REPCMT200001GB02TrappedFlag(), new REPCMT200001GB02PertinentInformation6.SeperatableInd()));
		
		// This class is a relationship of pertinent information. - It is used to indicate information the NHS111 encounter pertinent to the ambulance request.
		// possible from referral request encounter
		ambulanceRequestReport.setPertinentInformation5((REPCMT200001GB02PertinentInformation) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation(), 
						new REPCMT200001GB02EncounterEvent(), new REPCMT200001GB02PertinentInformation.SeperatableInd()));
		
		// This class is a relationship of pertinent information. - It is used to carry additional notes pertinent to the ambulance request. CAN BE NULL
		ambulanceRequestReport.getPertinentInformation7().add(null); // referral request note
		
		// This class is a relationship of pertinent information. - It is used to carry the Permission To View outcome. CAN BE NULL
		ambulanceRequestReport.setPertinentInformation8(null);
		
		// ambulanceRequestReport.setPertinentInformation9(null); // referral request clinical discriminator (reason reference) 
		
		// This class is act relationship of reason. - This class indicates the triage outcome is the reason for the ambulance request.
		REPCMT200001GB02Reason reason = new REPCMT200001GB02Reason();
		reason.setTypeCode(reason.getTypeCode());
		ambulanceRequestReport.setReason(reason);
		
		// This class is a replacement relationship class. - It is used when the ambulance request message is being replaced by an updated version. CAN BE NULL
		ambulanceRequestReport.setReplacementOf(null);
		
		return ambulanceRequestReport;
	}
	
	private <B extends BL, F extends Flag> PertinentInformation<B, F> createPertinentInformation(PertinentInformation<B, F> info, F flag, B b) {
		info.setTypeCode(info.getTypeCode());
		info.setSeperatableInd(b);
        info.getSeperatableInd().setValue(false);
        
        info.setFlag(flag);
		info.getFlag().setMoodCode(info.getFlag().getMoodCode());
		info.getFlag().setClassCode(info.getFlag().getClassCode());
        
        return info;
	}

}
