package uk.nhs.ctp.service.report.decorators;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hl7.fhir.dstu3.model.Annotation;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AdditionalNotes;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AdditionalNotes.Code;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation8;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation8.SeperatableInd;
import uk.nhs.ctp.service.report.org.hl7.v3.ST;

@Component
public class PertinentInformation7AdditionalNotes implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		List<REPCMT200001GB02PertinentInformation8> additionalNotes = document.getPertinentInformation7();
		additionalNotes.clear();
		
		REPCMT200001GB02PertinentInformation8 note = new REPCMT200001GB02PertinentInformation8();
		
		note.setTypeCode(note.getTypeCode());
		SeperatableInd noteSeperatableInd = new SeperatableInd();
		noteSeperatableInd.setValue(false);
		note.setSeperatableInd(noteSeperatableInd);
		
		REPCMT200001GB02AdditionalNotes noteContent = new REPCMT200001GB02AdditionalNotes();
		
		noteContent.setClassCode(noteContent.getClassCode());
		noteContent.setMoodCode("EVN");
		Code code = new Code();
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.422");
		code.setCode("OAN");
		code.setDisplayName("Other Additional Notes");
		noteContent.setCode(code);
		
		ST noteText = new ST();
		try {
			noteText.getAny().add(createElement(request.getReferralRequest().getNote()));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noteContent.setText(noteText);
		
		note.setPertinentAdditionalNotes(noteContent);
		
		additionalNotes.add(note);
	}
	
	private Element createElement(List<Annotation> list) throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element element = doc.createElement( "Notes" );
		
		list.stream().forEach(note -> {
			element.appendChild(doc.createTextNode(note.getText()));
		});
		
		return element;
	}

}
