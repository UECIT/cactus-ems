package uk.nhs.ctp.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import org.springframework.scheduling.annotation.Async;
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
	
	@Value("${clear.folder.timer}")
	Integer clearFolderTimer;
	
	@PostConstruct
	public void initialise() throws JAXBException, IOException, TransformerConfigurationException {
		context =  JAXBContext.newInstance(CONTEXT_PATH);
		
		marshaller = context.createMarshaller();
		marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
	}
	
	public String generateXml(JAXBElement<?> document) throws JAXBException {
		StringResult result = new StringResult();
		marshaller.marshal(document, result);
		
		return result.getWriter().toString();
	}
	
	@Async
	public void clearFolder() {
		File file = new File("src/main/resources/templates/");
		Arrays.asList(file.listFiles()).stream().forEach(item -> {
			if (item.lastModified() < System.currentTimeMillis() - clearFolderTimer) {
				item.delete();
			}
		});
	}
	
	public String generateHtml(JAXBElement<?> document, Resource templateResource) throws JAXBException, TransformerException, IOException {
		
		TransformerFactory tf = TransformerFactory.newInstance();
        StreamSource streamSource = new StreamSource(templateResource.getInputStream());
        transformer = tf.newTransformer(streamSource);
        
		String documentId = UUID.randomUUID().toString();		
		StreamResult result = new StreamResult(new File("src/main/resources/templates/" + documentId + ".html"));
		JAXBSource source = new JAXBSource(context, document);
		
		transformer.transform(source, result);
		
		clearFolder();

		return documentId;
	}
}
