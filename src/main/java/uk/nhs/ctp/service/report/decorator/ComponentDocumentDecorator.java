package uk.nhs.ctp.service.report.decorator;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.BundleToSectionMapper;
import uk.nhs.ctp.service.report.decorator.mapping.ReferralRequestToSectionMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.resolver.CodedEntryTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.II;
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
	private BundleToSectionMapper bundleToSectionMapper;
	
	@Autowired
	private ReferralRequestToSectionMapper referralRequestToSectionMapper;
	
	@Autowired
	private CodedEntryTemplateResolver<? extends IBaseResource> codedEntryTemplateResolver;
	
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
		sectionId.setRoot("1254345346434645634563456");
		classificationSection.setId(sectionId);
		
		Bundle resourceBundle = request.getBundle();
		
		POCDMT200001GB02Component31 triageSectionComponent = createSectionComponent();
		COCDTP146246GB01Section1 triageSection = bundleToSectionMapper.map(resourceBundle);
		triageSection.setAuthor(new JAXBElement<POCDMT200001GB02Author>(
				new QName("author"), POCDMT200001GB02Author.class, authorDocumentDecorator.createAuthor(request)));
		triageSectionComponent.setCOCDTP146246GB01Section1(triageSection);
		classificationSection.getComponent().add(triageSectionComponent);
		
		POCDMT200001GB02Component31 noteSectionComponent = createSectionComponent();
		noteSectionComponent.setCOCDTP146246GB01Section1(
				referralRequestToSectionMapper.map(request.getReferralRequest()));
		classificationSection.getComponent().add(noteSectionComponent);
		
		ResourceProviderUtils.getResources(resourceBundle, Observation.class).stream().forEach(observation -> {
			POCDMT200001GB02Component2 dataComponent = new POCDMT200001GB02Component2();
			dataComponent.setTypeCode(dataComponent.getTypeCode());
			
			classificationSection.getEntry().add(codedEntryTemplateResolver.resolve(observation, dataComponent, request));
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
	
	private POCDMT200001GB02Component31 createSectionComponent() {
		POCDMT200001GB02Component31 sectionComponent = new POCDMT200001GB02Component31();
		sectionComponent.setTypeCode(sectionComponent.getTypeCode());
		sectionComponent.setContextConductionInd(true);
	
		II sectionComponentId = new II();
		sectionComponentId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		sectionComponentId.setExtension("COCD_TP146246GB01"); 
		
		return sectionComponent;
	}
	
}
