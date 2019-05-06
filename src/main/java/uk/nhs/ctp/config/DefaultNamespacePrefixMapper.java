package uk.nhs.ctp.config;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Implementation of {@link NamespacePrefixMapper} that maps the schema
 * namespaces more to readable names. Used by the jaxb marshaller. Requires
 * setting the property "com.sun.xml.bind.namespacePrefixMapper" to an instance
 * of this class.
 * <p>
 * Requires dependency on JAXB implementation jars
 * </p>
 */
public class DefaultNamespacePrefixMapper extends NamespacePrefixMapper {

	private Map<String, String> namespaceMap = new HashMap<>();

	/**
	 * Create mappings.
	 */
	public DefaultNamespacePrefixMapper() {
		namespaceMap.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
		namespaceMap.put("http://www.intertech.com/consulting", "consult");
		namespaceMap.put("http://www.w3.org/2003/05/soap-envelope/", "soap");
		namespaceMap.put("http://schemas.xmlsoap.org/soap/envelope/", "x");
		namespaceMap.put("https://nww.pathwaysdos.nhs.uk/app/api/webservices", "web");
	}

	/*
	 * (non-Javadoc) Returning null when not found based on spec.
	 * 
	 * @see
	 * com.sun.xml.bind.marshaller.NamespacePrefixMapper#getPreferredPrefix(java.
	 * lang.String, java.lang.String, boolean)
	 */
	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
		return namespaceMap.getOrDefault(namespaceUri, suggestion);
	}

}