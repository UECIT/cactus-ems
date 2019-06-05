package uk.nhs.ctp.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringResult;

@Component
public class DocumentGenerator {

	private static final String CONTEXT_PATH = "uk.nhs.ctp.service.report.org.hl7.v3";
	
	private Marshaller marshaller;
	
	private JAXBContext context;
	
	private Transformer transformer;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Value("classpath:cda.xsl")
	Resource templateResource;
	
	@PostConstruct
	public void initialise() throws JAXBException, IOException, TransformerConfigurationException {
		context =  JAXBContext.newInstance(CONTEXT_PATH);
		
		marshaller = context.createMarshaller();
		marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		
		TransformerFactory tf = TransformerFactory.newInstance();
        StreamSource streamSource = new StreamSource(templateResource.getInputStream());
        transformer = tf.newTransformer(streamSource);
	}
	
	public String generateXml(JAXBElement<?> document) throws JAXBException {
		StringResult result = new StringResult();
		marshaller.marshal(document, result);
		
		return result.getWriter().toString();
	}
	
	public String generateHtml(JAXBElement<?> document) throws JAXBException, TransformerException {
		String documentId = UUID.randomUUID().toString();		
		StreamResult result = new StreamResult(new File("src/main/resources/templates/" + documentId + ".html"));
		JAXBSource source = new JAXBSource(context, document);
		
		transformer.transform(source, result);

		return documentId;
	}
}
