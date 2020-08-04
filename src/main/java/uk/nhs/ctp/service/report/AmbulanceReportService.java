package uk.nhs.ctp.service.report;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.mifmif.common.regex.Generex;

import uk.nhs.ctp.enums.ContentType;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
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
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02SceneSafeFlag;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TrappedFlag;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TraumaFlag;
import uk.nhs.ctp.utils.DocumentGenerator;

@Service
public class AmbulanceReportService implements Reportable {
	
	@Autowired
	private List<AmbulanceDecorator> decorators;
	
	@Autowired
	private Generex uuidGenerator;
	
	@Autowired
	private DocumentGenerator documentGenerator;
	
	@Autowired
	private SimpleDateFormat reportDateFormat;
	
	@Value("classpath:cdaAmbulance.xsl")
	Resource templateResource;
	
	private ObjectFactory objectFactory = new ObjectFactory();
	
	@Override
	public ReportsDTO generate(ReportRequestDTO request) throws JAXBException {
		REPCMT200001GB02AmbulanceRequest ambulanceRequest = objectFactory.createREPCMT200001GB02AmbulanceRequest();
		
		ambulanceRequest.setClassCode(ambulanceRequest.getClassCode());
		ambulanceRequest.setMoodCode(ambulanceRequest.getMoodCode());
		
		CV code = new CV();
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.15");
		code.setCode("828801000000101");
		ambulanceRequest.setCode(code);
		
		EffectiveTime effectiveTime = new EffectiveTime();

		effectiveTime.setValue(reportDateFormat.format(new Date()));
		ambulanceRequest.setEffectiveTime(effectiveTime);
		
		IINPfITUuidMandatory identifier = new IINPfITUuidMandatory();
		identifier.setRoot(uuidGenerator.random());
		ambulanceRequest.setId(identifier);
		
		setupPertinentInformation(ambulanceRequest);
		
		for (AmbulanceDecorator decorator : decorators) {
			decorator.decorate(ambulanceRequest, request);	
		}
		
		ambulanceRequest.setReplacementOf(null);
		
		JAXBElement<REPCMT200001GB02AmbulanceRequest> rootElement = 
				objectFactory.createAmbulanceRequest(ambulanceRequest);
		
		String htmlDocumentId = null;
		
		try {
			htmlDocumentId = documentGenerator.generateHtml(rootElement, templateResource);
		} catch (TransformerException | IOException e) {
		} 
		
		return new ReportsDTO(documentGenerator.generateXml(
				rootElement), ReportType.AMBULANCE_V3, ContentType.XML, htmlDocumentId);
	}

	private void setupPertinentInformation(REPCMT200001GB02AmbulanceRequest ambulanceRequestReport) {
		ambulanceRequestReport.setPertinentInformation((REPCMT200001GB02PertinentInformation2) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation2(), 
						new REPCMT200001GB02TraumaFlag(), new REPCMT200001GB02PertinentInformation2.SeperatableInd()));
		
		ambulanceRequestReport.setPertinentInformation1((REPCMT200001GB02PertinentInformation3) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation3(), 
						new REPCMT200001GB02FireFlag(), new REPCMT200001GB02PertinentInformation3.SeperatableInd()));
		
		ambulanceRequestReport.setPertinentInformation2((REPCMT200001GB02PertinentInformation4) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation4(), 
						new REPCMT200001GB02SceneSafeFlag(), new REPCMT200001GB02PertinentInformation4.SeperatableInd()));
		
		ambulanceRequestReport.setPertinentInformation3((REPCMT200001GB02PertinentInformation5) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation5(), 
						new REPCMT200001GB02PoliceFlag(), new REPCMT200001GB02PertinentInformation5.SeperatableInd()));
		
		ambulanceRequestReport.setPertinentInformation4((REPCMT200001GB02PertinentInformation6) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation6(), 
						new REPCMT200001GB02TrappedFlag(), new REPCMT200001GB02PertinentInformation6.SeperatableInd()));
		
		ambulanceRequestReport.setPertinentInformation5((REPCMT200001GB02PertinentInformation) 
				createPertinentInformation(new REPCMT200001GB02PertinentInformation(), 
						new REPCMT200001GB02EncounterEvent(), new REPCMT200001GB02PertinentInformation.SeperatableInd()));
		
		ambulanceRequestReport.getPertinentInformation7().add(null); // referral request note
		
		ambulanceRequestReport.setPertinentInformation8(null);
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
