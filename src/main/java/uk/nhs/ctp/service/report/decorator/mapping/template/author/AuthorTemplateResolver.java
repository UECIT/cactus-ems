package uk.nhs.ctp.service.report.decorator.mapping.template.author;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author.FunctionCode;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author.Time;

@Component
public class AuthorTemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02Author> {
	
	@Autowired
	public AuthorTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02Author>> authorChoiceTemplateMappers) {
		
		super(authorChoiceTemplateMappers);
	}
	
	@Autowired
	private SimpleDateFormat reportDateFormat;
	
	protected POCDMT200001GB02Author createContainer() {
		POCDMT200001GB02Author author  = new POCDMT200001GB02Author();
		
		// The HL7 attribute typeCode uses a code to describe this class as an author participation.
		author.setTypeCode(author.getTypeCode());
		author.getContextControlCode().add("OP");

		// The HL7 attribute functionCode uses a code from the vocabulary AuthorFunctionType to describe the function of the author.
		FunctionCode functionCode = new FunctionCode();
		functionCode.setCode("OA");
		functionCode.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.178");
		functionCode.setDisplayName("Originating Author");
		author.setFunctionCode(functionCode);

		// The HL7 attribute author time is used to indicate when the person authored the CDA document.
		Time time = new Time();
		time.setValue(reportDateFormat.format(new Date()));
		author.setTime(time);
		
		return author;
	}
}


