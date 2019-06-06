package uk.nhs.ctp.service.report;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.hl7.fhir.dstu3.model.codesystems.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;
import uk.nhs.ctp.service.report.decorator.OneOneOneDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.ObjectFactory;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.utils.DocumentGenerator;

public abstract class OneOneOneReportService implements Reportable {
	
	@Autowired
	private Collection<OneOneOneDecorator> decorators;
	
	@Autowired
	private DocumentGenerator documentGenerator;
	
	@Value("classpath:cda.xsl")
	Resource templateResource;
	
	private ObjectFactory objectFactory = new ObjectFactory();
	
	public ReportsDTO generate(ReportRequestDTO request) throws JAXBException {
		request.setTemplateMappingExclusions(getTemplateMappingExclusions());
		POCDMT200001GB02ClinicalDocument document = 
				objectFactory.createPOCDMT200001GB02ClinicalDocument();

		// This class is a related document relationship 
		// it links the current document to a related CDA document.
		// (CAN BE NULL)
		document.setRelatedDocument(null);
		decorators.stream().forEach(decorator -> decorator.decorate(document, request));

		JAXBElement<POCDMT200001GB02ClinicalDocument> rootElement = objectFactory.createClinicalDocument(document);
		
		String htmlDocumentId = null;
		
        try {
        	htmlDocumentId = documentGenerator.generateHtml(rootElement, templateResource);
		} catch (TransformerException | IOException e) {
		} 
        
		return new ReportsDTO(
				documentGenerator.generateXml(rootElement), getReportType(), ContentType.XML, htmlDocumentId);
	}

	protected abstract Set<Class<?>> getTemplateMappingExclusions();

	protected abstract ReportType getReportType();

}
