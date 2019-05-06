package uk.nhs.ctp.service.report.decorators;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146092GB01ClinicalDiscriminator;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.ObjectFactory;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClassificationSection;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component2;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component31;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component4;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component5;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02StructuredBody;
import uk.nhs.ctp.service.report.org.hl7.v3.ST;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;

@Component
public class ComponentDocumentDecorator implements OneOneOneDecorator {

	private ObjectFactory objectFactory = new ObjectFactory();
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		
		POCDMT200001GB02Component5 component = objectFactory.createPOCDMT200001GB02Component5();
		// The HL7 attribute typeCode uses a code to describe this class as a component.
		component.setTypeCode(component.getTypeCode());
		// The HL7 attribute contextConductionInd uses a Boolean value (true or false) to determine whether information associated with the document header is conducted across to the structured body (also associated with the structured body).
		component.setContextConductionInd(true);
		// The StructuredBody represents a CDA document body that is comprised of one or more document sections. 
		POCDMT200001GB02StructuredBody structuredBody = createStructuredBody();
		structuredBody.setClassCode(structuredBody.getClassCode());
		structuredBody.setMoodCode(structuredBody.getMoodCode());
		POCDMT200001GB02Component4 component4 = new POCDMT200001GB02Component4();
		component4.setTypeCode(component4.getTypeCode());
		POCDMT200001GB02ClassificationSection classificationSection = new POCDMT200001GB02ClassificationSection();
		classificationSection.setClassCode(classificationSection.getClassCode());
		classificationSection.setMoodCode(classificationSection.getMoodCode());
		
		// Add Speciality 
		addClinicalDiscriminatorEntry(classificationSection, request.getReferralRequest().getSpecialty().getCodingFirstRep().getDisplay(), request.getReferralRequest().getSpecialty().getCodingFirstRep().getCode());
		
		// Add Supporting Info
		request.getReferralRequest().getSupportingInfo().stream().forEach(supportingInfo -> {
			addClinicalDiscriminatorEntry(classificationSection, supportingInfo.getDisplay(), null);
		});
		
		// Add Reason Reference
		request.getReferralRequest().getReasonReference().stream().forEach(reasonReference -> {
			addClinicalDiscriminatorEntry(classificationSection, reasonReference.getDisplay(), null);
		});
		// Add notes
		request.getReferralRequest().getNote().stream().forEach(note -> {
			addTextComponentEntry(classificationSection, note.getText(), null);
		});
		

		component4.setClassificationSection(classificationSection);

		structuredBody.setComponent(component4);

		component.setStructuredBody(structuredBody);
		document.setComponent(component);
	}
	
	private void addClinicalDiscriminatorEntry(POCDMT200001GB02ClassificationSection classificationSection, String displayName, String code) {
		POCDMT200001GB02Component2 entry = new POCDMT200001GB02Component2();
		entry.setTypeCode(entry.getTypeCode());
		COCDTP146092GB01ClinicalDiscriminator clinicalDiscriminator = new COCDTP146092GB01ClinicalDiscriminator();
		clinicalDiscriminator.setClassCode(clinicalDiscriminator.getClassCode());
		clinicalDiscriminator.setMoodCode(clinicalDiscriminator.getMoodCode());
		CV cv = new CV();
		cv.setDisplayName(displayName);
		cv.setCode(code);
		clinicalDiscriminator.setValue(cv);
		entry.setCOCDTP146092GB01ClinicalDiscriminator(clinicalDiscriminator);
		
		classificationSection.getEntry().add(entry);
	}
	
	private void addTextComponentEntry(POCDMT200001GB02ClassificationSection classificationSection, String displayName, String code) {
		POCDMT200001GB02Component31 entry = new POCDMT200001GB02Component31();
		entry.setTypeCode(entry.getTypeCode());
		entry.setContextConductionInd(entry.isContextConductionInd());
		
		COCDTP146246GB01Section1 textSection = new COCDTP146246GB01Section1();
		ST sectionTitle = new ST();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Element c = dom.createElement("titletest");
		c.setTextContent("testTextContent");
		sectionTitle.getAny().add(c);
		textSection.setTitle(sectionTitle);
		
		textSection.setClassCode(textSection.getClassCode());
		textSection.setMoodCode(textSection.getMoodCode());
		textSection.setText(new StrucDocText());
		textSection.getText().getContent().add(displayName);
		entry.setCOCDTP146246GB01Section1(textSection);
		
		classificationSection.getComponent().add(entry);
	}


	private POCDMT200001GB02StructuredBody createStructuredBody() {
		POCDMT200001GB02StructuredBody structuredBody = new POCDMT200001GB02StructuredBody();
		structuredBody.setClassCode(structuredBody.getClassCode());
		structuredBody.setMoodCode(structuredBody.getMoodCode());
		POCDMT200001GB02Component4 component4 = new POCDMT200001GB02Component4();
		structuredBody.setComponent(component4);

		return structuredBody;
	}
	
}
