package uk.nhs.ctp.service.report.decorators.author;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorators.AmbulanceDecorator;
import uk.nhs.ctp.service.report.decorators.OneOneOneDecorator;
import uk.nhs.ctp.service.report.decorators.author.mapping.AuthorDataResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author.FunctionCode;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author.Time;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;

@Component
public class AuthorDocumentDecorator implements OneOneOneDecorator, AmbulanceDecorator {
	
	@Autowired
	private AuthorDataResolver<? extends Resource> authorDataResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.getAuthor().add(createAuthor(request));
	}

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		document.getAuthor().add(createAuthor(request));
	}
	
	public POCDMT200001GB02Author createAuthor(ReportRequestDTO request) {
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
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		time.setValue(format.format(new Date()));
		author.setTime(time);
		
		authorDataResolver.resolve(request.getReferralRequest(), author);

		return author;
	}

}
