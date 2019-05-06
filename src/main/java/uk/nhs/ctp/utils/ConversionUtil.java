package uk.nhs.ctp.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.xml.transform.StringResult;

public class ConversionUtil {

	public static String convertToXml(JAXBElement<?> document, String contextPath) throws JAXBException {
		JAXBContext jc =  JAXBContext.newInstance(contextPath);
		StringResult result = new StringResult();
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		marshaller.marshal(document, result);
		result.getWriter().toString();
		return result.getWriter().toString();
	}
}
