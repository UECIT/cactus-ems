package uk.nhs.ctp.service.report;

import java.util.Collection;

import javax.xml.bind.JAXBException;

import org.hl7.fhir.dstu3.model.codesystems.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.decorator.OneOneOneDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.ObjectFactory;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.utils.ConversionUtil;

@Service
public class OneOneOneReportService implements Reportable {

	@Autowired
	private Collection<OneOneOneDecorator> decorators;
	
	private ObjectFactory objectFactory = new ObjectFactory();
	
	public ReportsDTO generate(ReportRequestDTO request) throws JAXBException {
		// convert ReferralRequest to ClinicalDocument
		POCDMT200001GB02ClinicalDocument document = 
				objectFactory.createPOCDMT200001GB02ClinicalDocument();

		document.getParticipant().add(null);
		// This class is a related document relationship 
		// it links the current document to a related CDA document.
		// (CAN BE NULL)
		document.setRelatedDocument(null);
		// This class is a tracker participation. 
		// A tracker is a recipient who is sent a copy of the CDA document for information only. 
		// They are not normally required to carry out any action on recipient of the CDA document. 
		// (CAN BE NULL)
		document.getTracker().add(null);

		decorators.stream().forEach(decorator -> decorator.decorate(document, request));
		
		return new ReportsDTO(ConversionUtil.convertToXml(objectFactory.createClinicalDocument(document), 
				"uk.nhs.ctp.service.report.org.hl7.v3"), null, ReportType.ONE_ONE_ONE, ContentType.XML);
	}

}
