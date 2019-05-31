package uk.nhs.ctp.service.report.decorator;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mifmif.common.regex.Generex;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.codedentry.CodedEntryTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.textsection.IntegratedUrgentCareTextSectionTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITUuidMandatory;
import uk.nhs.ctp.service.report.org.hl7.v3.ObjectFactory;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClassificationSection;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component2;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component31;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component4;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component5;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02StructuredBody;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class ComponentDocumentDecorator implements OneOneOneDecorator {

	private ObjectFactory objectFactory = new ObjectFactory();
	
	@Autowired
	private AuthorDocumentDecorator authorDocumentDecorator;
	
	@Autowired
	private CodedEntryTemplateResolver<? extends IBaseResource> codedEntryTemplateResolver;
	
	@Autowired
	private IntegratedUrgentCareTextSectionTemplateResolver<? extends IBaseResource> textSectionTemplateResolver;
	
	@Autowired
	private Generex uuidGenerator;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		
		POCDMT200001GB02Component5 component = objectFactory.createPOCDMT200001GB02Component5();
		component.setTypeCode(component.getTypeCode());
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
		
		IINPfITUuidMandatory sectionId = new IINPfITUuidMandatory();
		sectionId.setRoot(uuidGenerator.random());
		classificationSection.setId(sectionId);
		
		Bundle resourceBundle = request.getBundle();
		JAXBElement<POCDMT200001GB02Author> authorElement = new JAXBElement<>(
				new QName("urn:hl7-org:v3", "author"), POCDMT200001GB02Author.class, authorDocumentDecorator.createAuthor(request));
		
		POCDMT200001GB02Component31 triageSectionComponent = 
				textSectionTemplateResolver.resolve(resourceBundle, request);
		triageSectionComponent.getCOCDTP146246GB01Section1().setAuthor(authorElement);
		classificationSection.getComponent().add(triageSectionComponent);
		
		POCDMT200001GB02Component31 noteSectionComponent = 
				textSectionTemplateResolver.resolve(request.getReferralRequest(), request);
		noteSectionComponent.getCOCDTP146246GB01Section1().setAuthor(authorElement);
		classificationSection.getComponent().add(noteSectionComponent);
		
		ResourceProviderUtils.getResources(resourceBundle, CareConnectObservation.class).stream().forEach(observation -> {
			POCDMT200001GB02Component2 component2 = codedEntryTemplateResolver.resolve(observation, request);
			if (component2 != null)	
				classificationSection.getEntry().add(component2);
		});

		component4.setClassificationSection(classificationSection);
		structuredBody.setComponent(component4);
		component.setStructuredBody(structuredBody);
		document.setComponent(component);
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
