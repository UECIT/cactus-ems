package uk.nhs.ctp.service;

import java.util.Optional;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.ConceptMap;
import org.hl7.fhir.dstu3.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplainRequired;

@Service
public class TerminologyService {

	@Value("${ems.terminology.service.url}")
	private String terminologyServiceUrl;

	@Value("${ems.terminology.service.url.valueset}")
	private String terminologyServiceValueSetUrl;

	@Value("${ems.terminology.service.url.valueset.reference}")
	private String terminologyServiceValueSetRef;

	@Value("${ems.terminology.service.url.conceptmap}")
	private String terminologyServiceConceptMapUrl;

	@Value("${ems.terminology.service.url.conceptmap.source}")
	private String terminologyServiceUrlConceptMapSource;

	@Value("${ems.terminology.service.url.conceptmap.target}")
	private String terminologyServiceUrlConceptMapTarget;

	private IGenericClient client;

	@Autowired
	private FhirContext fhirContext;

	public CVNPfITCodedplainRequired getCode(String sourceSystem, String targetSystem, String code) {
		client = fhirContext.newRestfulGenericClient(terminologyServiceUrl);

		// STEP1 get the valueSet that is tied to the sourceSystem via search
		ValueSet valueSet = getValueSet(sourceSystem);
		// STEP2 perform the ConceptMap Search
		ConceptMap conceptMap = valueSet != null ? getConceptMap(targetSystem, valueSet):null;
		// STEP3 find matching code in ConceptMap
		return conceptMap != null ? getTargetCode(code, targetSystem, conceptMap):null;
	}

	private ValueSet getValueSet(String sourceSystem) {
		String ValueSetSearch = terminologyServiceValueSetUrl + "?" + terminologyServiceValueSetRef + sourceSystem;
		Bundle valueSetBundle = (Bundle) client.search().byUrl(ValueSetSearch).execute();
		ValueSet valueSet = valueSetBundle.hasEntry() ? (ValueSet) valueSetBundle.getEntryFirstRep().getResource():null;
		return valueSet;
	}

	private ConceptMap getConceptMap(String targetSystem, ValueSet valueSet) {
		String ConceptMapSearch = terminologyServiceConceptMapUrl + "?" + terminologyServiceUrlConceptMapSource
				+ valueSet.getUrl() + "&" + terminologyServiceUrlConceptMapTarget + targetSystem;
		Bundle conceptMapBundle = (Bundle) client.search().byUrl(ConceptMapSearch).execute();
		ConceptMap conceptMap = conceptMapBundle.hasEntry() ? (ConceptMap) conceptMapBundle.getEntryFirstRep().getResource(): null;
		return conceptMap;
	}

	private CVNPfITCodedplainRequired getTargetCode(String code, String targetSystem, ConceptMap conceptMap) {
		CVNPfITCodedplainRequired targetCode = null;
		Optional<SourceElementComponent> optional = conceptMap.getGroupFirstRep().getElement().stream()
				.filter(data -> data.getCode().equalsIgnoreCase(code)).findFirst();
		if (optional.isPresent()) {
			targetCode = new CVNPfITCodedplainRequired();
			targetCode.setCode(optional.get().getTargetFirstRep().getCode());
			targetCode.setDisplayName(optional.get().getTargetFirstRep().getDisplay());
			targetCode.setCodeSystem(targetSystem);
		}
		return targetCode;
	}

}
